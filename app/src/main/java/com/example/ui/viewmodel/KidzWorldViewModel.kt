package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.audio.KidzAudioEngine
import com.example.core.audio.SoundEffect
import com.example.core.localization.AppLanguage
import com.example.data.repository.KidzWorldRepository
import com.example.domain.model.AppSettings
import com.example.domain.model.ChildProfile
import com.example.domain.model.DailyStreak
import com.example.domain.model.GameScore
import com.example.domain.model.GameType
import com.example.domain.model.LessonItem
import com.example.domain.model.ModuleProgress
import com.example.domain.model.ModuleType
import com.example.domain.model.Story
import com.example.domain.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KidzWorldViewModel(
    private val repository: KidzWorldRepository,
    val audioEngine: KidzAudioEngine
) : ViewModel() {

    private val _initState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val initState: StateFlow<UiState<Unit>> = _initState.asStateFlow()

    val settings: StateFlow<AppSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSettings()
    )

    val profiles: StateFlow<List<ChildProfile>> = repository.profilesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activeChild: StateFlow<ChildProfile?> = combine(profiles, settings) { list, s ->
        list.firstOrNull { it.id == s.activeChildId } ?: list.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val lessons: StateFlow<List<LessonItem>> = repository.activeLessonsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val stories: StateFlow<List<Story>> = repository.activeStoriesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentScores: StateFlow<List<GameScore>> = repository.activeScoresFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val dailyStreak: StateFlow<DailyStreak> = repository.activeDailyStreakFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailyStreak()
    )

    val moduleProgressMap: StateFlow<Map<ModuleType, ModuleProgress>> = combine(lessons) { (allLessons) ->
        ModuleType.entries.associateWith { type ->
            val subset = allLessons.filter { it.moduleType == type }
            ModuleProgress(
                moduleType = type,
                completedCount = subset.count { it.isLearned },
                totalCount = subset.size
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val nextRecommendedLesson: StateFlow<LessonItem?> = combine(lessons, settings) { allLessons, s ->
        val preferredOrder = if (s.language == AppLanguage.BANGLA) {
            listOf(
                ModuleType.ALPHABET_BN,
                ModuleType.ALPHABET_EN,
                ModuleType.NUMBERS,
                ModuleType.ANIMALS,
                ModuleType.FRUITS_VEGGIES,
                ModuleType.COLORS,
                ModuleType.SHAPES,
                ModuleType.BANGLA_VOCAB,
                ModuleType.BASIC_WORDS
            )
        } else {
            listOf(
                ModuleType.ALPHABET_EN,
                ModuleType.ALPHABET_BN,
                ModuleType.NUMBERS,
                ModuleType.ANIMALS,
                ModuleType.FRUITS_VEGGIES,
                ModuleType.COLORS,
                ModuleType.SHAPES,
                ModuleType.BASIC_WORDS,
                ModuleType.SIMPLE_SENTENCES
            )
        }
        preferredOrder.firstNotNullOfOrNull { mod ->
            allLessons.firstOrNull { it.moduleType == mod && !it.isLearned }
        } ?: allLessons.firstOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _celebrationMessage = MutableStateFlow<String?>(null)
    val celebrationMessage: StateFlow<String?> = _celebrationMessage.asStateFlow()

    private val _showOnboardingReplay = MutableStateFlow(false)
    val showOnboardingReplay: StateFlow<Boolean> = _showOnboardingReplay.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = audioEngine.isSpeaking
    val banglaVoiceAvailable: StateFlow<Boolean> = audioEngine.banglaVoiceAvailable

    init {
        viewModelScope.launch {
            settings.collect { s ->
                audioEngine.soundFxEnabled = s.soundFxEnabled
                audioEngine.ttsEnabled = s.ttsEnabled
                audioEngine.speechRate = s.speechRate
            }
        }
        initializeWorld()
    }

    fun initializeWorld() {
        viewModelScope.launch {
            _initState.value = UiState.Loading
            try {
                repository.ensureSeeded()
                _initState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _initState.value = UiState.Error(
                    e.localizedMessage ?: "Unable to load local learning library."
                )
            }
        }
    }

    fun toggleLanguage() {
        audioEngine.playEffect(SoundEffect.TAP_POP)
        val next = if (settings.value.language == AppLanguage.ENGLISH) {
            AppLanguage.BANGLA
        } else {
            AppLanguage.ENGLISH
        }
        viewModelScope.launch { repository.setLanguage(next) }
    }

    fun setLanguage(language: AppLanguage) {
        audioEngine.playEffect(SoundEffect.TAP_POP)
        viewModelScope.launch { repository.setLanguage(language) }
    }

    fun selectChild(childId: Long) {
        audioEngine.playEffect(SoundEffect.TAP_POP)
        viewModelScope.launch { repository.setActiveChild(childId) }
    }

    fun addChild(name: String, age: Int, avatarKey: String) {
        if (name.isBlank()) return
        audioEngine.playEffect(SoundEffect.CORRECT_CHIME)
        viewModelScope.launch {
            repository.addChildProfile(name = name, age = age, avatarKey = avatarKey)
        }
    }

    fun deleteChild(childId: Long) {
        val currentList = profiles.value
        if (currentList.size <= 1) return
        val fallback = currentList.firstOrNull { it.id != childId }?.id ?: return
        viewModelScope.launch {
            repository.deleteChildProfile(childId = childId, fallbackChildId = fallback)
        }
    }

    fun speakLesson(item: LessonItem) {
        val lang = settings.value.language
        val speechLang = when (item.moduleType) {
            ModuleType.ALPHABET_BN, ModuleType.BANGLA_VOCAB -> AppLanguage.BANGLA
            ModuleType.ALPHABET_EN -> AppLanguage.ENGLISH
            else -> lang
        }
        audioEngine.playEffect(SoundEffect.TAP_POP)
        audioEngine.speak(item.speechPrompt(speechLang), speechLang)
    }

    fun speakText(text: String, language: AppLanguage = settings.value.language) {
        audioEngine.speak(text, language)
    }

    fun stopSpeaking() {
        audioEngine.stopSpeaking()
    }

    fun playEffect(effect: SoundEffect) {
        audioEngine.playEffect(effect)
    }

    fun markLessonLearned(item: LessonItem) {
        val childId = activeChild.value?.id ?: return
        val lang = settings.value.language
        viewModelScope.launch {
            val newlyLearned = repository.markLessonLearned(childId, item.id)
            audioEngine.playEffect(SoundEffect.CELEBRATION_FANFARE)
            val msg = if (lang == AppLanguage.BANGLA) {
                if (newlyLearned) "দারুণ! +৫ তারা অর্জন করেছ!" else "আবার অনুশীলন করলে! শাবাশ!"
            } else {
                if (newlyLearned) "Awesome! +5 Stars earned!" else "Great practice! Keep shining!"
            }
            triggerCelebration(msg)
        }
    }

    fun completeStory(story: Story) {
        val childId = activeChild.value?.id ?: return
        val lang = settings.value.language
        viewModelScope.launch {
            val newlyCompleted = repository.markStoryCompleted(childId, story.id)
            audioEngine.playEffect(SoundEffect.CELEBRATION_FANFARE)
            val msg = if (lang == AppLanguage.BANGLA) {
                if (newlyCompleted) "গল্প শেষ! +১৫ তারা পেয়েছ!" else "সুন্দর গল্পটি আবার পড়লে!"
            } else {
                if (newlyCompleted) "Story Finished! +15 Stars!" else "Wonderful story time!"
            }
            triggerCelebration(msg)
        }
    }

    fun recordGameResult(gameType: GameType, score: Int, starsEarned: Int) {
        val childId = activeChild.value?.id ?: return
        val lang = settings.value.language
        viewModelScope.launch {
            repository.recordGameScore(childId, gameType, score, starsEarned)
            audioEngine.playEffect(SoundEffect.CELEBRATION_FANFARE)
            val msg = if (lang == AppLanguage.BANGLA) {
                "চ্যাম্পিয়ন! +$starsEarned তারা জিতেছ!"
            } else {
                "Champion! +$starsEarned Stars won!"
            }
            triggerCelebration(msg)
        }
    }

    fun claimDailyStreakBonus() {
        val childId = activeChild.value?.id ?: return
        val lang = settings.value.language
        viewModelScope.launch {
            val awarded = repository.claimDailyStreakBonus(childId)
            if (awarded > 0) {
                audioEngine.playEffect(SoundEffect.CELEBRATION_FANFARE)
                val msg = if (lang == AppLanguage.BANGLA) {
                    "দৈনিক ধারা বোনাস! +$awarded তারা পেয়েছ!"
                } else {
                    "Daily Streak Bonus! +$awarded Stars!"
                }
                triggerCelebration(msg)
            }
        }
    }

    fun resetActiveChildProgress() {
        val childId = activeChild.value?.id ?: return
        viewModelScope.launch {
            repository.resetChildProgress(childId)
            audioEngine.playEffect(SoundEffect.TAP_POP)
        }
    }

    fun setSoundFxEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setSoundFxEnabled(enabled) }
    }

    fun setTtsEnabled(enabled: Boolean) {
        if (!enabled) audioEngine.stopSpeaking()
        viewModelScope.launch { repository.setTtsEnabled(enabled) }
    }

    fun setSpeechRate(rate: Float) {
        viewModelScope.launch { repository.setSpeechRate(rate) }
    }

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkModeEnabled(enabled) }
    }

    fun completeOnboarding(childName: String, age: Int, avatarKey: String) {
        viewModelScope.launch {
            val trimmed = childName.trim()
            if (trimmed.isNotEmpty()) {
                val existing = profiles.value.firstOrNull { it.name.equals(trimmed, ignoreCase = true) }
                if (existing != null) {
                    repository.setActiveChild(existing.id)
                } else {
                    repository.addChildProfile(name = trimmed, age = age, avatarKey = avatarKey)
                }
            }
            repository.setOnboardingCompleted(true)
            _showOnboardingReplay.value = false
            audioEngine.playEffect(SoundEffect.CELEBRATION_FANFARE)
            val msg = if (settings.value.language == AppLanguage.BANGLA) {
                "স্বাগতম কিড'জ ওয়ার্ল্ডে!"
            } else {
                "Welcome to KID'Z WORLD!"
            }
            triggerCelebration(msg)
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            repository.setOnboardingCompleted(true)
            _showOnboardingReplay.value = false
            audioEngine.playEffect(SoundEffect.TAP_POP)
        }
    }

    fun replayOnboarding() {
        audioEngine.stopSpeaking()
        audioEngine.playEffect(SoundEffect.TAP_POP)
        _showOnboardingReplay.value = true
    }

    private fun triggerCelebration(message: String) {
        viewModelScope.launch {
            _celebrationMessage.value = message
            delay(2300L)
            if (_celebrationMessage.value == message) {
                _celebrationMessage.value = null
            }
        }
    }

    class Factory(
        private val repository: KidzWorldRepository,
        private val audioEngine: KidzAudioEngine
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return KidzWorldViewModel(repository, audioEngine) as T
        }
    }
}
