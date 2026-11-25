package com.example.core.audio

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.example.core.localization.AppLanguage
import com.example.domain.model.VoicePackStatus
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
    private var preferredBanglaVoice: Voice? = null
    private var preferredEnglishVoice: Voice? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _activeUtteranceIndex = MutableStateFlow<Int?>(null)
    val activeUtteranceIndex: StateFlow<Int?> = _activeUtteranceIndex.asStateFlow()

    private val _banglaVoiceAvailable = MutableStateFlow(true)
    val banglaVoiceAvailable: StateFlow<Boolean> = _banglaVoiceAvailable.asStateFlow()

    private val _voiceStatus = MutableStateFlow(VoicePackStatus())
    val voiceStatus: StateFlow<VoicePackStatus> = _voiceStatus.asStateFlow()

    @Volatile var soundFxEnabled: Boolean = true
    @Volatile var ttsEnabled: Boolean = true
    @Volatile var speechRate: Float = 0.84f

    init {
        initializeEngine()
    }

    private fun initializeEngine() {
        try {
            // Prefer Google Speech Services when installed for clearest natural Bangla & English neural voices
            val hasGoogleTts = try {
                appContext.packageManager.getPackageInfo(GOOGLE_TTS_PACKAGE, 0)
                true
            } catch (_: Exception) {
                false
            }
            tts = if (hasGoogleTts) {
                TextToSpeech(appContext, this, GOOGLE_TTS_PACKAGE)
            } else {
                TextToSpeech(appContext, this)
            }
        } catch (e: Exception) {
            Log.w(TAG, "TTS initialization skipped: ${e.message}")
            try {
                tts = TextToSpeech(appContext, this)
            } catch (_: Exception) {
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _activeUtteranceIndex.value = parseSequenceIndex(utteranceId)
                }

                override fun onDone(utteranceId: String?) {
                    val idx = parseSequenceIndex(utteranceId)
                    if (idx == null || _activeUtteranceIndex.value == idx) {
                        _isSpeaking.value = false
                        _activeUtteranceIndex.value = null
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _activeUtteranceIndex.value = null
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeaking.value = false
                    _activeUtteranceIndex.value = null
                }
            })
            refreshVoices()
        } else {
            isTtsReady = false
            _voiceStatus.value = VoicePackStatus(
                banglaInstalled = false,
                englishInstalled = false,
                isInitialized = true
            )
        }
    }

    /**
     * Re-scans installed Android TTS language packs and picks the clearest, most natural
     * offline voice for both Bangla (bn-BD / bn-IN) and English (en-US / en-GB).
     */
    fun refreshVoices() {
        val engine = tts
        if (engine == null || !isTtsReady) {
            reinitializeEngine()
            return
        }
        try {
            val voices = runCatching { engine.voices }.getOrNull().orEmpty()
            preferredBanglaVoice = selectBestVoice(
                voices = voices,
                languageCode = "bn",
                preferredCountries = listOf("BD", "IN")
            )
            preferredEnglishVoice = selectBestVoice(
                voices = voices,
                languageCode = "en",
                preferredCountries = listOf("US", "GB", "AU")
            )

            val bnBdStatus = engine.isLanguageAvailable(Locale.forLanguageTag("bn-BD"))
            val bnInStatus = engine.isLanguageAvailable(Locale.forLanguageTag("bn-IN"))
            val enUsStatus = engine.isLanguageAvailable(Locale.US)

            val banglaReady = preferredBanglaVoice != null ||
                bnBdStatus >= TextToSpeech.LANG_AVAILABLE ||
                bnInStatus >= TextToSpeech.LANG_AVAILABLE
            val englishReady = preferredEnglishVoice != null ||
                enUsStatus >= TextToSpeech.LANG_AVAILABLE

            val isHighQualityBn = preferredBanglaVoice?.let {
                it.quality >= Voice.QUALITY_HIGH && !it.isNetworkConnectionRequired
            } ?: false

            _banglaVoiceAvailable.value = banglaReady
            _voiceStatus.value = VoicePackStatus(
                banglaInstalled = banglaReady,
                englishInstalled = englishReady,
                isHighQualityBangla = isHighQualityBn,
                selectedBanglaVoiceLabel = formatVoiceLabel(preferredBanglaVoice, "bn-BD"),
                selectedEnglishVoiceLabel = formatVoiceLabel(preferredEnglishVoice, "en-US"),
                isInitialized = true
            )
        } catch (e: Exception) {
            Log.w(TAG, "Voice scan failed: ${e.message}")
        }
    }

    private fun reinitializeEngine() {
        try {
            tts?.shutdown()
        } catch (_: Exception) {
        }
        tts = null
        isTtsReady = false
        initializeEngine()
    }

    private fun selectBestVoice(
        voices: Set<Voice>,
        languageCode: String,
        preferredCountries: List<String>
    ): Voice? {
        val candidates = voices.filter { voice ->
            voice.locale?.language.equals(languageCode, ignoreCase = true) &&
                !voice.features.contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)
        }
        if (candidates.isEmpty()) return null

        return candidates.maxByOrNull { voice ->
            var score = 0
            // Strongly prefer offline local language packs (instant, clear, zero network stutter)
            if (!voice.isNetworkConnectionRequired) score += 120 else score -= 60

            // Prefer high / very-high quality neural voices over low-quality robotic voices
            score += when {
                voice.quality >= Voice.QUALITY_VERY_HIGH -> 100
                voice.quality >= Voice.QUALITY_HIGH -> 75
                voice.quality >= Voice.QUALITY_NORMAL -> 35
                else -> -30
            }

            // Prefer lower latency voices for responsive child interaction
            score += when {
                voice.latency <= Voice.LATENCY_VERY_LOW -> 25
                voice.latency <= Voice.LATENCY_LOW -> 20
                voice.latency <= Voice.LATENCY_NORMAL -> 10
                else -> -10
            }

            // Country priority: bn-BD over bn-IN; en-US over en-GB
            val country = voice.locale?.country.orEmpty().uppercase(Locale.US)
            val countryRank = preferredCountries.indexOf(country)
            if (countryRank >= 0) {
                score += (preferredCountries.size - countryRank) * 20
            }

            // Prefer Google local neural voices and avoid robotic legacy synth voices
            val lowerName = voice.name.lowercase(Locale.US)
            if ("local" in lowerName) score += 20
            if ("network" in lowerName) score -= 25
            if ("espeak" in lowerName || "pico" in lowerName) score -= 90
            // Warm female/child-friendly voices on Google TTS (e.g. bn-bd-x-ban-local / en-us-x-tpf-local)
            if ("ban" in lowerName || "tpf" in lowerName || "sfg" in lowerName || "x-bn" in lowerName) {
                score += 12
            }

            score
        }
    }

    private fun formatVoiceLabel(voice: Voice?, fallbackLocale: String): String {
        if (voice == null) return fallbackLocale
        val localeTag = voice.locale?.toLanguageTag() ?: fallbackLocale
        val qualityTag = when {
            voice.quality >= Voice.QUALITY_VERY_HIGH -> "HD"
            voice.quality >= Voice.QUALITY_HIGH -> "Clear"
            else -> "Standard"
        }
        return "$localeTag ($qualityTag)"
    }

    private fun applyNaturalVoice(engine: TextToSpeech, language: AppLanguage, rateMultiplier: Float) {
        val chosenVoice = when (language) {
            AppLanguage.BANGLA -> preferredBanglaVoice
            AppLanguage.ENGLISH -> preferredEnglishVoice
        }
        if (chosenVoice != null) {
            val voiceResult = runCatching { engine.setVoice(chosenVoice) }.getOrNull()
            if (voiceResult != TextToSpeech.SUCCESS) {
                applyLocaleFallback(engine, language)
            }
        } else {
            applyLocaleFallback(engine, language)
        }

        val effectiveRate = (speechRate * rateMultiplier).coerceIn(0.62f, 1.18f)
        engine.setSpeechRate(effectiveRate)
        // Warm, natural pitch (1.03–1.04) avoids robotic/tinny artifacts while staying kid-friendly
        val naturalPitch = if (language == AppLanguage.BANGLA) 1.03f else 1.04f
        engine.setPitch(naturalPitch)
    }

    private fun applyLocaleFallback(engine: TextToSpeech, language: AppLanguage) {
        val primary = when (language) {
            AppLanguage.BANGLA -> Locale.forLanguageTag("bn-BD")
            AppLanguage.ENGLISH -> Locale.US
        }
        val status = engine.setLanguage(primary)
        if (status == TextToSpeech.LANG_MISSING_DATA || status == TextToSpeech.LANG_NOT_SUPPORTED) {
            if (language == AppLanguage.BANGLA) {
                val bnIn = engine.setLanguage(Locale.forLanguageTag("bn-IN"))
                if (bnIn < TextToSpeech.LANG_AVAILABLE) {
                    _banglaVoiceAvailable.value = false
                    _voiceStatus.value = _voiceStatus.value.copy(banglaInstalled = false)
                }
            } else {
                engine.setLanguage(Locale.UK)
            }
        }
    }

    /**
     * Shapes text with gentle punctuation pauses so Android TTS speaks naturally and rhythmically.
     */
    private fun sanitizeForNaturalSpeech(text: String, language: AppLanguage): String {
        val cleaned = text
            .replace("•", ", ")
            .replace("\n", if (language == AppLanguage.BANGLA) "। " else ", ")
            .replace(Regex("\\s+"), " ")
            .trim()
        return cleaned
    }

    fun speak(
        text: String,
        language: AppLanguage,
        rateMultiplier: Float = 1.0f
    ) {
        if (!ttsEnabled || !isTtsReady || text.isBlank()) return
        val engine = tts ?: return
        try {
            _activeUtteranceIndex.value = null
            applyNaturalVoice(engine, language, rateMultiplier)
            val naturalText = sanitizeForNaturalSpeech(text, language)
            engine.speak(naturalText, TextToSpeech.QUEUE_FLUSH, null, "kidz_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w(TAG, "Speak failed: ${e.message}")
            _isSpeaking.value = false
        }
    }

    /**
     * Queues a multi-stanza poem or story with rhythmic pauses and emits live stanza indices
     * via [activeUtteranceIndex] so the UI can highlight each stanza as it is recited aloud.
     */
    fun speakSequence(
        lines: List<String>,
        language: AppLanguage,
        rateMultiplier: Float = 0.94f
    ) {
        if (!ttsEnabled || !isTtsReady || lines.isEmpty()) return
        val engine = tts ?: return
        try {
            engine.stop()
            applyNaturalVoice(engine, language, rateMultiplier)
            lines.forEachIndexed { index, rawLine ->
                val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                val utteranceId = "${SEQ_PREFIX}${index}_${System.currentTimeMillis()}"
                val spoken = sanitizeForNaturalSpeech(rawLine, language)
                engine.speak(spoken, queueMode, null, utteranceId)
                if (index < lines.lastIndex) {
                    engine.playSilentUtterance(420L, TextToSpeech.QUEUE_ADD, "pause_${index}")
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Sequence speak failed: ${e.message}")
            _isSpeaking.value = false
            _activeUtteranceIndex.value = null
        }
    }

    fun stopSpeaking() {
        try {
            tts?.stop()
        } catch (_: Exception) {
        }
        _isSpeaking.value = false
        _activeUtteranceIndex.value = null
    }

    /**
     * Opens Android's official TTS language pack installer so parents can download the
     * offline Bangla voice pack in one tap, falling back to system TTS settings if needed.
     */
    fun openBanglaVoicePackInstaller(activityContext: Context) {
        val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        val settingsIntent = Intent("com.android.settings.TTS_SETTINGS").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            activityContext.startActivity(installIntent)
        } catch (_: Exception) {
            try {
                activityContext.startActivity(settingsIntent)
            } catch (e: Exception) {
                Log.w(TAG, "Unable to open TTS settings: ${e.message}")
            }
        }
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

    private fun parseSequenceIndex(utteranceId: String?): Int? {
        if (utteranceId == null || !utteranceId.startsWith(SEQ_PREFIX)) return null
        val rest = utteranceId.removePrefix(SEQ_PREFIX)
        return rest.substringBefore('_').toIntOrNull()
    }

    companion object {
        private const val TAG = "KidzAudioEngine"
        private const val GOOGLE_TTS_PACKAGE = "com.google.android.tts"
        private const val SEQ_PREFIX = "kidz_seq_"
    }
}
