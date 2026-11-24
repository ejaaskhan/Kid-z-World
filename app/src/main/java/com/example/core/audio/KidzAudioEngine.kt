package com.example.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.core.localization.AppLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class KidzAudioEngine(context: Context) : TextToSpeech.OnInitListener {

    private val appContext = context.applicationContext
    private val audioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _banglaVoiceAvailable = MutableStateFlow(true)
    val banglaVoiceAvailable: StateFlow<Boolean> = _banglaVoiceAvailable.asStateFlow()

    @Volatile var soundFxEnabled: Boolean = true
    @Volatile var ttsEnabled: Boolean = true
    @Volatile var speechRate: Float = 0.88f

    init {
        try {
            tts = TextToSpeech(appContext, this)
        } catch (e: Exception) {
            Log.w(TAG, "TTS initialization skipped: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                }
            })
            val bnResult = tts?.isLanguageAvailable(Locale.forLanguageTag("bn-BD"))
                ?: TextToSpeech.LANG_NOT_SUPPORTED
            _banglaVoiceAvailable.value = bnResult >= TextToSpeech.LANG_AVAILABLE
        } else {
            isTtsReady = false
        }
    }

    fun speak(text: String, language: AppLanguage) {
        if (!ttsEnabled || !isTtsReady || text.isBlank()) return
        val engine = tts ?: return
        try {
            val targetLocale = when (language) {
                AppLanguage.BANGLA -> Locale.forLanguageTag("bn-BD")
                AppLanguage.ENGLISH -> Locale.US
            }
            val availability = engine.setLanguage(targetLocale)
            if (availability == TextToSpeech.LANG_MISSING_DATA ||
                availability == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                if (language == AppLanguage.BANGLA) {
                    _banglaVoiceAvailable.value = false
                    val inLocale = engine.setLanguage(Locale.forLanguageTag("bn-IN"))
                    if (inLocale < TextToSpeech.LANG_AVAILABLE) {
                        engine.setLanguage(Locale.US)
                    }
                } else {
                    engine.setLanguage(Locale.US)
                }
            }
            engine.setSpeechRate(speechRate.coerceIn(0.6f, 1.3f))
            engine.setPitch(1.08f)
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kidz_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w(TAG, "Speak failed: ${e.message}")
            _isSpeaking.value = false
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
        _isSpeaking.value = false
    }

    fun playEffect(effect: SoundEffect) {
        if (!soundFxEnabled) return
        audioScope.launch {
            try {
                when (effect) {
                    SoundEffect.TAP_POP -> playNotes(listOf(587.33 to 65))
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
                // Warm marimba-like harmonic blend
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
        try {
            tts?.shutdown()
        } catch (_: Exception) {
        }
        tts = null
    }

    companion object {
        private const val TAG = "KidzAudioEngine"
    }
}
