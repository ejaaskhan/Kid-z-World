package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.core.localization.AppLanguage
import com.example.domain.model.BundledBanglaAudioStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

enum class SoundEffect {
    TAP_POP,
    CORRECT_CHIME,
    TRY_AGAIN_BOOP,
    CELEBRATION_FANFARE,
    PAGE_TURN
}

/**
 * Centralized KID'Z WORLD audio service.
 *
 * - **Bangla Audio (100% Built-In Offline MP3s)**: Played directly from bundled APK assets
 *   (`assets/audio/bangla/...`) via [BundledBanglaAudioPlayer] + [BanglaAudioManifest].
 *   Does NOT use Android System TTS, Google Speech Services, voice-pack installers, or
 *   system settings for Bangla.
 * - **English Audio**: Uses local offline English TTS (`en-US` / `en-GB`) solely for English mode.
 * - **Sound FX**: Synthesized PCM feedback chimes via [AudioTrack].
 */
class KidzAudioEngine(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val banglaManifest: BanglaAudioManifest = BanglaAudioManifest.loadFromAssets(
        appContext.assets,
        BanglaAudioManifest.MANIFEST_ASSET_PATH
    )
    val englishManifest: BanglaAudioManifest = BanglaAudioManifest.loadFromAssets(
        appContext.assets,
        BanglaAudioManifest.ENGLISH_MANIFEST_ASSET_PATH
    )
    private val banglaPlayer = BundledBanglaAudioPlayer(appContext)

    // Last-resort English TTS fallback ONLY for arbitrary unmapped strings
    private var englishTts: TextToSpeech? = null
    private var isEnglishTtsReady = false
    private var preferredEnglishVoice: Voice? = null

    private val _isEnglishSpeaking = MutableStateFlow(false)

    val isSpeaking: StateFlow<Boolean> = combine(
        banglaPlayer.isPlaying,
        _isEnglishSpeaking
    ) { bnPlaying, enPlaying -> bnPlaying || enPlaying }.stateIn(
        scope = audioScope,
        started = SharingStarted.Eagerly,
        initialValue = false
    )

    val activeUtteranceIndex: StateFlow<Int?> = banglaPlayer.activeSequenceIndex

    private val _bundledAudioStatus = MutableStateFlow(
        BundledBanglaAudioStatus(
            isReady = banglaManifest.totalEntries > 0,
            verifiedClipsCount = banglaManifest.totalEntries + englishManifest.totalEntries,
            totalClipsCount = banglaManifest.totalEntries + englishManifest.totalEntries,
            localeLabel = "বাংলা (bn-BD) & English (en-US) Offline Human Voices"
        )
    )
    val bundledAudioStatus: StateFlow<BundledBanglaAudioStatus> = _bundledAudioStatus.asStateFlow()

    @Volatile var soundFxEnabled: Boolean = true
    @Volatile var ttsEnabled: Boolean = true
    @Volatile var speechRate: Float = 0.86f

    private var triedGoogleEngineForEnglish = false

    init {
        // Verify bundled Bangla + English human MP3 assets asynchronously on IO thread
        audioScope.launch {
            val bnAudit = banglaManifest.auditBundledAssets(appContext.assets)
            val enAudit = englishManifest.auditBundledAssets(appContext.assets)
            _bundledAudioStatus.value = BundledBanglaAudioStatus(
                isReady = bnAudit.verifiedCount > 0 && enAudit.verifiedCount > 0,
                verifiedClipsCount = bnAudit.verifiedCount + enAudit.verifiedCount,
                totalClipsCount = bnAudit.totalCount + enAudit.totalCount,
                localeLabel = "বাংলা (bn-BD) & English (en-US) Offline Human Voices"
            )
            if (bnAudit.missingAssetPaths.isNotEmpty() || enAudit.missingAssetPaths.isNotEmpty()) {
                Log.w(
                    TAG,
                    "Missing ${bnAudit.missingAssetPaths.size} Bangla & ${enAudit.missingAssetPaths.size} English bundled MP3 clips"
                )
            }
        }
        initializeEnglishTtsOnly()
    }

    private fun initializeEnglishTtsOnly() {
        try {
            // Prefer Google Speech Services for natural neural English voices when present;
            // automatically fall back to the device's default TTS engine if unavailable.
            triedGoogleEngineForEnglish = true
            englishTts = TextToSpeech(appContext, this, GOOGLE_SPEECH_ENGINE)
        } catch (e: Exception) {
            Log.w(TAG, "Preferred English TTS engine unavailable, using system default: ${e.message}")
            triedGoogleEngineForEnglish = false
            try {
                englishTts = TextToSpeech(appContext, this)
            } catch (fallbackEx: Exception) {
                Log.w(TAG, "English TTS init skipped: ${fallbackEx.message}")
            }
        }
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            if (triedGoogleEngineForEnglish) {
                triedGoogleEngineForEnglish = false
                try {
                    englishTts?.shutdown()
                } catch (_: Exception) {
                }
                try {
                    englishTts = TextToSpeech(appContext, this)
                    return
                } catch (e: Exception) {
                    Log.w(TAG, "Default English TTS fallback failed: ${e.message}")
                }
            }
            isEnglishTtsReady = false
            return
        }
        isEnglishTtsReady = true
        val engine = englishTts ?: return
        engine.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
        )
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isEnglishSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isEnglishSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isEnglishSpeaking.value = false
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isEnglishSpeaking.value = false
            }
        })

        try {
            val voices = runCatching { engine.voices }.getOrNull().orEmpty()
            preferredEnglishVoice = selectBestEnglishVoice(voices)
            if (preferredEnglishVoice != null) {
                engine.setVoice(preferredEnglishVoice)
            } else {
                val res = engine.setLanguage(Locale.US)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.UK)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "English voice selection failed: ${e.message}")
        }
    }

    private fun selectBestEnglishVoice(voices: Set<Voice>): Voice? {
        val candidates = voices.filter { voice ->
            voice.locale?.language.equals("en", ignoreCase = true) &&
                !voice.features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
        }
        if (candidates.isEmpty()) return null
        return candidates.maxByOrNull { voice ->
            var score = 0
            // Strongly prefer offline, high-clarity local voices so speech never stalls without internet
            if (!voice.isNetworkConnectionRequired) score += 140 else score -= 70
            score += when {
                voice.quality >= Voice.QUALITY_VERY_HIGH -> 130
                voice.quality >= Voice.QUALITY_HIGH -> 95
                voice.quality >= Voice.QUALITY_NORMAL -> 40
                else -> -35
            }
            // Prefer low latency for responsive child taps
            if (voice.latency <= Voice.LATENCY_NORMAL) score += 20
            val country = voice.locale?.country.orEmpty().uppercase(Locale.US)
            score += when (country) {
                "US" -> 30
                "GB" -> 22
                "AU", "CA" -> 12
                else -> 0
            }
            val lower = voice.name.lowercase(Locale.US)
            if ("local" in lower) score += 28
            if ("neural" in lower || "wavenet" in lower || "studio" in lower || "hd" in lower) score += 35
            // Google's high-clarity warm English female local voices (e.g. en-us-x-sfg / tpf / iob / en-gb-x-gba)
            if ("tpf" in lower || "iob" in lower || "sfg" in lower || "gba" in lower) score += 18
            // Strongly penalize robotic/metallic legacy voices
            if ("espeak" in lower || "pico" in lower || "compact" in lower || "robot" in lower) score -= 120
            score
        }
    }

    /**
     * Converts the user's speech speed setting (default 0.86f) into a natural MediaPlayer speed
     * multiplier for pre-recorded Bangladeshi Bangla MP3 clips centered cleanly at 1.0x studio cadence
     * (zero pitch distortion).
     */
    private fun banglaPlaybackSpeed(rateMultiplier: Float = 1.0f): Float {
        val normalized = (speechRate / DEFAULT_SPEECH_RATE) * rateMultiplier
        return normalized.coerceIn(0.76f, 1.18f)
    }

    /**
     * Prepares English text for natural, child-friendly spoken clarity with gentle phrasing pauses
     * and zero artificial pitch manipulation.
     */
    private fun formatNaturalEnglishSpeech(raw: String): String {
        return raw
            .replace("•", ", ")
            .replace("—", ", ")
            .replace("–", ", ")
            .replace("\n", ". ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    // =========================================================================
    // BUNDLED OFFLINE BANGLA PLAYBACK API (Zero Android System TTS)
    // =========================================================================

    fun playBanglaLesson(lessonId: String) {
        if (!ttsEnabled) return
        val assetPath = banglaManifest.assetForLesson(lessonId) ?: return
        stopEnglishTtsOnly()
        banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed())
    }

    fun playBanglaNumber(number: Int) {
        if (!ttsEnabled) return
        val assetPath = banglaManifest.assetForNumber(number) ?: return
        stopEnglishTtsOnly()
        banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed())
    }

    fun playBanglaPoem(poemId: String, stanzaCount: Int, slowMode: Boolean = false) {
        if (!ttsEnabled) return
        val assets = banglaManifest.assetsForPoem(poemId, stanzaCount)
        if (assets.isEmpty()) return
        stopEnglishTtsOnly()
        val speed = banglaPlaybackSpeed(if (slowMode) 0.85f else 1.0f)
        banglaPlayer.playSequence(
            assetPaths = assets,
            speed = speed,
            pauseBetweenMs = if (slowMode) 460L else 340L,
            emitIndices = true
        )
    }

    fun playBanglaPoemStanza(poemId: String, stanzaIndex: Int, slowMode: Boolean = false) {
        if (!ttsEnabled) return
        val assetPath = banglaManifest.assetForPoemStanza(poemId, stanzaIndex) ?: return
        stopEnglishTtsOnly()
        val speed = banglaPlaybackSpeed(if (slowMode) 0.85f else 1.0f)
        banglaPlayer.playAsset(assetPath, speed = speed)
    }

    fun playBanglaStoryPage(storyId: String, pageNumber: Int) {
        if (!ttsEnabled) return
        val assetPath = banglaManifest.assetForStoryPage(storyId, pageNumber) ?: return
        stopEnglishTtsOnly()
        banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed(0.98f))
    }

    fun playBanglaStoryMoral(storyId: String) {
        if (!ttsEnabled) return
        val assetPath = banglaManifest.assetForStoryMoral(storyId) ?: return
        stopEnglishTtsOnly()
        banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed(0.98f))
    }

    fun playBanglaInstruction(instructionId: String) {
        if (!ttsEnabled) return
        val assetPath = banglaManifest.assetForInstruction(instructionId) ?: return
        stopEnglishTtsOnly()
        banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed())
    }

    // =========================================================================
    // BUNDLED OFFLINE HUMAN ENGLISH PLAYBACK API (No Robotic TTS)
    // =========================================================================

    fun playEnglishLesson(lessonId: String, fallbackPrompt: String? = null) {
        if (!ttsEnabled) return
        val assetPath = englishManifest.assetForLesson(lessonId)
        if (assetPath != null) {
            stopEnglishTtsOnly()
            banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed())
        } else if (!fallbackPrompt.isNullOrBlank()) {
            speak(fallbackPrompt, AppLanguage.ENGLISH)
        }
    }

    fun playEnglishNumber(number: Int) {
        if (!ttsEnabled) return
        val assetPath = englishManifest.entryById("num_$number")?.assetPath
        if (assetPath != null) {
            stopEnglishTtsOnly()
            banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed())
        } else {
            speak(number.toString(), AppLanguage.ENGLISH)
        }
    }

    fun playEnglishStoryPage(storyId: String, pageNumber: Int, fallbackText: String) {
        if (!ttsEnabled) return
        val assetPath = englishManifest.assetForStoryPage(storyId, pageNumber)
        if (assetPath != null) {
            stopEnglishTtsOnly()
            banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed(0.98f))
        } else {
            speak(fallbackText, AppLanguage.ENGLISH)
        }
    }

    fun playEnglishInstruction(instructionId: String, fallbackText: String? = null) {
        if (!ttsEnabled) return
        val assetPath = englishManifest.assetForInstruction(instructionId)
        if (assetPath != null) {
            stopEnglishTtsOnly()
            banglaPlayer.playAsset(assetPath, speed = banglaPlaybackSpeed())
        } else if (!fallbackText.isNullOrBlank()) {
            speak(fallbackText, AppLanguage.ENGLISH)
        }
    }

    /**
     * Unified human-voice speech entry point:
     * - For [AppLanguage.BANGLA]: Resolves ONLY to bundled offline human Bangla MP3s.
     * - For [AppLanguage.ENGLISH]: Resolves FIRST to bundled offline human English MP3s;
     *   falls back to local natural TTS only if a dynamic unmapped string has no clip.
     */
    fun speak(
        text: String,
        language: AppLanguage,
        rateMultiplier: Float = 1.0f
    ) {
        if (!ttsEnabled || text.isBlank()) return
        if (language == AppLanguage.BANGLA) {
            stopEnglishTtsOnly()
            val assets = banglaManifest.resolveAssetsForBanglaText(text)
            if (assets.isEmpty()) {
                Log.d(TAG, "No bundled Bangla MP3 matched for text: '$text' (System TTS disabled for Bangla)")
                return
            }
            val speed = banglaPlaybackSpeed(rateMultiplier)
            if (assets.size == 1) {
                banglaPlayer.playAsset(assets.first(), speed = speed)
            } else {
                banglaPlayer.playSequence(
                    assetPaths = assets,
                    speed = speed,
                    pauseBetweenMs = 140L,
                    emitIndices = false
                )
            }
            return
        }

        // First try bundled offline human English MP3s (zero robotic TTS!)
        val englishAssets = englishManifest.resolveAssetsForEnglishText(text)
        if (englishAssets.isNotEmpty()) {
            stopEnglishTtsOnly()
            val speed = banglaPlaybackSpeed(rateMultiplier)
            if (englishAssets.size == 1) {
                banglaPlayer.playAsset(englishAssets.first(), speed = speed)
            } else {
                banglaPlayer.playSequence(
                    assetPaths = englishAssets,
                    speed = speed,
                    pauseBetweenMs = 140L,
                    emitIndices = false
                )
            }
            return
        }

        // Fallback only for unmapped dynamic English strings (natural 1.0f pitch)
        banglaPlayer.stop()
        if (!isEnglishTtsReady) return
        val engine = englishTts ?: return
        try {
            preferredEnglishVoice?.let { runCatching { engine.setVoice(it) } }
                ?: runCatching { engine.setLanguage(Locale.US) }
            val effectiveRate = (speechRate * rateMultiplier).coerceIn(0.68f, 1.12f)
            engine.setSpeechRate(effectiveRate)
            engine.setPitch(1.0f)
            val cleaned = formatNaturalEnglishSpeech(text)
            engine.speak(cleaned, TextToSpeech.QUEUE_FLUSH, null, "kidz_en_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w(TAG, "English TTS fallback failed: ${e.message}")
            _isEnglishSpeaking.value = false
        }
    }

    fun stopSpeaking() {
        banglaPlayer.stop()
        stopEnglishTtsOnly()
    }

    private fun stopEnglishTtsOnly() {
        try {
            englishTts?.stop()
        } catch (_: Exception) {
        }
        _isEnglishSpeaking.value = false
    }

    // =========================================================================
    // SYNTHESIZED CHILD-FRIENDLY SOUND FX (PCM AudioTrack)
    // =========================================================================

    fun playEffect(effect: SoundEffect) {
        if (!soundFxEnabled) return
        // Button tap / click sounds are always silent
        if (effect == SoundEffect.TAP_POP) return
        audioScope.launch {
            try {
                when (effect) {
                    SoundEffect.TAP_POP -> Unit
                    SoundEffect.PAGE_TURN -> playNotes(listOf(440.0 to 50, 523.25 to 60))
                    SoundEffect.TRY_AGAIN_BOOP -> playNotes(listOf(311.13 to 90, 261.63 to 120))
                    SoundEffect.CORRECT_CHIME -> playNotes(listOf(523.25 to 80, 659.25 to 85, 783.99 to 130))
                    SoundEffect.CELEBRATION_FANFARE -> playNotes(
                        listOf(523.25 to 85, 659.25 to 85, 783.99 to 85, 1046.50 to 210)
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Sound FX playback skipped: ${e.message}")
            }
        }
    }

    private fun playNotes(notes: List<Pair<Double, Int>>) {
        val sampleRate = 22050
        val totalSamples = notes.sumOf { (sampleRate * it.second) / 1000 }
        if (totalSamples <= 0) return

        val pcm = ShortArray(totalSamples)
        var offset = 0
        for ((freq, durationMs) in notes) {
            val count = (sampleRate * durationMs) / 1000
            for (i in 0 until count) {
                val t = i.toDouble() / sampleRate
                val attack = (i / (sampleRate * 0.008)).coerceAtMost(1.0)
                val decay = exp(-6.5 * t)
                val wave = 0.78 * sin(2.0 * PI * freq * t) + 0.22 * sin(4.0 * PI * freq * t)
                val sample = (wave * attack * decay * Short.MAX_VALUE * 0.35).toInt()
                    .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                if (offset + i < pcm.size) {
                    pcm[offset + i] = sample.toShort()
                }
            }
            offset += count
        }

        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(pcm.size * 2)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBuf)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        try {
            track.write(pcm, 0, pcm.size)
            track.play()
            val totalMs = notes.sumOf { it.second }.toLong() + 60L
            Thread.sleep(totalMs)
        } finally {
            try {
                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    fun release() {
        stopSpeaking()
        banglaPlayer.release()
        try {
            englishTts?.shutdown()
        } catch (_: Exception) {
        }
        englishTts = null
    }

    companion object {
        private const val TAG = "KidzAudioEngine"
        private const val GOOGLE_SPEECH_ENGINE = "com.google.android.tts"
        const val DEFAULT_SPEECH_RATE = 0.86f
    }
}
