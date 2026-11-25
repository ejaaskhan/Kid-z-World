package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.atomic.AtomicLong
import kotlin.coroutines.resume

/**
 * Self-contained offline Bangladeshi Bangla MP3 audio player backed by Android's native
 * [MediaPlayer] reading directly from bundled APK assets (`assets/audio/bangla/...`).
 *
 * Guarantees:
 * - Zero internet required
 * - Zero Android System TTS or Google Speech Services
 * - Zero voice pack installation or settings redirects
 * - Single-active playback lock so clips never overlap
 * - Smooth replay, stop, child-friendly playback speed, and stanza-by-stanza sequences
 */
class BundledBanglaAudioPlayer(context: Context) {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val playerLock = Any()
    private val playbackGeneration = AtomicLong(0L)

    private var activePlayer: MediaPlayer? = null
    private var activeSequenceJob: Job? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _activeSequenceIndex = MutableStateFlow<Int?>(null)
    val activeSequenceIndex: StateFlow<Int?> = _activeSequenceIndex.asStateFlow()

    /**
     * Plays a single bundled MP3 asset immediately, stopping any previously running playback
     * to prevent audio overlap.
     */
    fun playAsset(
        assetPath: String,
        speed: Float = 1.0f
    ) {
        val token = cancelCurrentPlayback()
        _activeSequenceIndex.value = null
        activeSequenceJob = scope.launch {
            playSingleAssetSuspending(
                assetPath = assetPath,
                speed = speed,
                token = token
            )
            if (playbackGeneration.get() == token) {
                _isPlaying.value = false
                _activeSequenceIndex.value = null
            }
        }
    }

    /**
     * Plays a sequence of bundled MP3 assets (e.g., poem title + stanzas, or math equation parts)
     * with a gentle pause between tracks, emitting each active index via [activeSequenceIndex]
     * so Compose UI can highlight lines in real time.
     */
    fun playSequence(
        assetPaths: List<String>,
        speed: Float = 1.0f,
        pauseBetweenMs: Long = 320L,
        emitIndices: Boolean = true
    ) {
        if (assetPaths.isEmpty()) return
        val token = cancelCurrentPlayback()
        activeSequenceJob = scope.launch {
            for ((idx, path) in assetPaths.withIndex()) {
                if (!isActive || playbackGeneration.get() != token) break
                if (emitIndices) {
                    _activeSequenceIndex.value = idx
                }
                playSingleAssetSuspending(
                    assetPath = path,
                    speed = speed,
                    token = token
                )
                if (!isActive || playbackGeneration.get() != token) break
                if (idx < assetPaths.lastIndex && pauseBetweenMs > 0L) {
                    delay(pauseBetweenMs)
                }
            }
            if (playbackGeneration.get() == token) {
                _isPlaying.value = false
                _activeSequenceIndex.value = null
            }
        }
    }

    fun stop() {
        cancelCurrentPlayback()
        _isPlaying.value = false
        _activeSequenceIndex.value = null
    }

    fun release() {
        stop()
    }

    private fun cancelCurrentPlayback(): Long {
        val nextToken = playbackGeneration.incrementAndGet()
        activeSequenceJob?.cancel()
        activeSequenceJob = null
        releaseActivePlayerLocked()
        return nextToken
    }

    private fun releaseActivePlayerLocked() {
        synchronized(playerLock) {
            val mp = activePlayer
            activePlayer = null
            if (mp != null) {
                try {
                    if (mp.isPlaying) {
                        mp.stop()
                    }
                } catch (_: Exception) {
                }
                try {
                    mp.reset()
                    mp.release()
                } catch (_: Exception) {
                }
            }
        }
    }

    private suspend fun playSingleAssetSuspending(
        assetPath: String,
        speed: Float,
        token: Long
    ) = suspendCancellableCoroutine { cont ->
        if (playbackGeneration.get() != token) {
            if (cont.isActive) cont.resume(Unit)
            return@suspendCancellableCoroutine
        }

        val player = MediaPlayer()
        var tempFallbackFile: File? = null

        fun cleanupAndResume() {
            synchronized(playerLock) {
                if (activePlayer === player) {
                    activePlayer = null
                }
            }
            try {
                player.setOnCompletionListener(null)
                player.setOnErrorListener(null)
                player.release()
            } catch (_: Exception) {
            }
            try {
                tempFallbackFile?.delete()
            } catch (_: Exception) {
            }
            if (cont.isActive) {
                cont.resume(Unit)
            }
        }

        cont.invokeOnCancellation {
            try {
                if (player.isPlaying) player.stop()
            } catch (_: Exception) {
            }
            cleanupAndResume()
        }

        try {
            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )

            // Primary fast path: direct zero-copy AssetFileDescriptor from uncompressed APK asset
            var dataSourceSet = false
            try {
                appContext.assets.openFd(assetPath).use { afd ->
                    player.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    dataSourceSet = true
                }
            } catch (_: Exception) {
                // Fallback if an APK packager compressed the MP3: stream to a tiny cache file
                val cacheFile = File(appContext.cacheDir, "bn_clip_${token}.mp3")
                appContext.assets.open(assetPath).use { input ->
                    cacheFile.outputStream().use { output -> input.copyTo(output) }
                }
                tempFallbackFile = cacheFile
                player.setDataSource(cacheFile.absolutePath)
                dataSourceSet = true
            }

            if (!dataSourceSet) {
                cleanupAndResume()
                return@suspendCancellableCoroutine
            }

            player.setOnCompletionListener {
                cleanupAndResume()
            }
            player.setOnErrorListener { _, what, extra ->
                Log.w(TAG, "MediaPlayer error for $assetPath (what=$what, extra=$extra)")
                cleanupAndResume()
                true
            }

            player.prepare()

            val clampedSpeed = speed.coerceIn(0.72f, 1.25f)
            try {
                val params = PlaybackParams().apply {
                    this.speed = clampedSpeed
                    this.pitch = 1.0f
                }
                player.playbackParams = params
            } catch (e: Exception) {
                Log.w(TAG, "PlaybackParams speed skipped: ${e.message}")
            }

            synchronized(playerLock) {
                if (playbackGeneration.get() != token) {
                    cleanupAndResume()
                    return@suspendCancellableCoroutine
                }
                releaseActivePlayerLocked()
                activePlayer = player
            }

            _isPlaying.value = true
            player.start()
        } catch (e: Exception) {
            Log.w(TAG, "Unable to play bundled Bangla asset '$assetPath': ${e.message}")
            cleanupAndResume()
        }
    }

    companion object {
        private const val TAG = "BundledBanglaAudio"
    }
}
