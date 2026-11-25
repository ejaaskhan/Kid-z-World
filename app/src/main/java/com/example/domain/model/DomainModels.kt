package com.example.domain.model

import com.example.core.localization.AppLanguage

enum class LearningPillar(val key: String) {
    ALPHABET("alphabet"),
    NUMBERS("numbers"),
    WORLD("world"),
    LANGUAGE("language");

    fun moduleTypes(): List<ModuleType> = when (this) {
        ALPHABET -> listOf(ModuleType.ALPHABET_EN, ModuleType.ALPHABET_BN)
        NUMBERS -> listOf(ModuleType.NUMBERS)
        WORLD -> listOf(
            ModuleType.ANIMALS,
            ModuleType.FRUITS_VEGGIES,
            ModuleType.COLORS,
            ModuleType.SHAPES,
            ModuleType.HUMAN_BODY,
            ModuleType.VEHICLES,
            ModuleType.NATURE
        )
        LANGUAGE -> listOf(
            ModuleType.BASIC_WORDS,
            ModuleType.EVERYDAY_VOCAB,
            ModuleType.SIMPLE_SENTENCES,
            ModuleType.BANGLA_VOCAB
        )
    }

    companion object {
        fun forModule(type: ModuleType): LearningPillar = when (type) {
            ModuleType.ALPHABET_EN, ModuleType.ALPHABET_BN -> ALPHABET
            ModuleType.NUMBERS -> NUMBERS
            ModuleType.ANIMALS,
            ModuleType.FRUITS_VEGGIES,
            ModuleType.COLORS,
            ModuleType.SHAPES,
            ModuleType.HUMAN_BODY,
            ModuleType.VEHICLES,
            ModuleType.NATURE -> WORLD
            ModuleType.BASIC_WORDS,
            ModuleType.EVERYDAY_VOCAB,
            ModuleType.SIMPLE_SENTENCES,
            ModuleType.BANGLA_VOCAB -> LANGUAGE
        }
    }
}

enum class AgeDifficulty(
    val key: String,
    val ageTag: String,
    val maxNumber: Int,
    val maxMathOperand: Int
) {
    STARTER("starter", "3–4", maxNumber = 20, maxMathOperand = 5),
    EXPLORER("explorer", "5–6", maxNumber = 50, maxMathOperand = 12),
    SCHOLAR("scholar", "7–8", maxNumber = 100, maxMathOperand = 45);

    companion object {
        fun fromAge(age: Int): AgeDifficulty = when {
            age <= 4 -> STARTER
            age <= 6 -> EXPLORER
            else -> SCHOLAR
        }
    }
}

enum class ModuleType(val key: String) {
    ALPHABET_EN("alphabet_en"),
    ALPHABET_BN("alphabet_bn"),
    NUMBERS("numbers"),
    ANIMALS("animals"),
    FRUITS_VEGGIES("fruits_veggies"),
    COLORS("colors"),
    SHAPES("shapes"),
    HUMAN_BODY("human_body"),
    VEHICLES("vehicles"),
    NATURE("nature"),
    BASIC_WORDS("basic_words"),
    EVERYDAY_VOCAB("everyday_vocab"),
    SIMPLE_SENTENCES("simple_sentences"),
    BANGLA_VOCAB("bangla_vocab");

    companion object {
        fun fromKey(key: String): ModuleType =
            entries.firstOrNull { it.key == key } ?: ALPHABET_EN
    }
}

enum class GameType(val key: String) {
    MEMORY_MATCH("memory_match"),
    PICTURE_QUIZ("picture_quiz"),
    JIGSAW_PUZZLE("jigsaw_puzzle"),
    SORTING_PUZZLE("sorting_puzzle"),
    PATTERN_PUZZLE("pattern_puzzle"),
    ALPHABET_QUIZ("alphabet_quiz"),
    MATH_QUIZ("math_quiz"),
    WORLD_QUIZ("world_quiz"),
    LANGUAGE_QUIZ("language_quiz");

    companion object {
        fun fromKey(key: String): GameType =
            entries.firstOrNull { it.key == key } ?: MEMORY_MATCH
    }
}

data class ChildProfile(
    val id: Long = 0L,
    val name: String,
    val age: Int,
    val avatarKey: String,
    val totalStars: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

enum class ThemeMode(val key: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    fun resolveDarkTheme(isSystemDark: Boolean): Boolean = when (this) {
        LIGHT -> false
        DARK -> true
        SYSTEM -> isSystemDark
    }

    companion object {
        fun fromKey(key: String?): ThemeMode =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: SYSTEM
    }
}

data class AppSettings(
    val language: AppLanguage = AppLanguage.BANGLA,
    val activeChildId: Long = 1L,
    val soundFxEnabled: Boolean = true,
    val ttsEnabled: Boolean = true,
    val speechRate: Float = 0.86f,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val onboardingCompleted: Boolean = false
) {
    val darkModeEnabled: Boolean get() = themeMode == ThemeMode.DARK
}

data class LessonItem(
    val id: String,
    val moduleType: ModuleType,
    val symbol: String,
    val symbolBn: String,
    val titleEn: String,
    val titleBn: String,
    val phoneticEn: String,
    val phoneticBn: String,
    val exampleEn: String,
    val exampleBn: String,
    val visualBadge: String,
    val accentColorHex: Long,
    val countValue: Int = 0,
    val sortOrder: Int = 0,
    val isLearned: Boolean = false
) {
    fun displaySymbol(language: AppLanguage): String = when (moduleType) {
        ModuleType.ALPHABET_EN -> symbol
        ModuleType.ALPHABET_BN -> symbolBn
        ModuleType.NUMBERS -> if (language == AppLanguage.BANGLA) symbolBn else symbol
        else -> visualBadge
    }

    fun displayTitle(language: AppLanguage): String =
        if (language == AppLanguage.BANGLA) titleBn else titleEn

    fun displayExample(language: AppLanguage): String =
        if (language == AppLanguage.BANGLA) exampleBn else exampleEn

    fun speechPrompt(language: AppLanguage): String = when (moduleType) {
        ModuleType.ALPHABET_EN -> if (language == AppLanguage.BANGLA) "$symbol। $exampleBn।" else "$symbol, is for $exampleEn."
        ModuleType.ALPHABET_BN -> if (language == AppLanguage.BANGLA) "$symbolBn, $symbolBn তে $exampleBn।" else "$titleEn, $exampleEn."
        ModuleType.NUMBERS -> if (language == AppLanguage.BANGLA) "$titleBn।" else "$titleEn."
        ModuleType.SIMPLE_SENTENCES -> if (language == AppLanguage.BANGLA) titleBn else titleEn
        ModuleType.BANGLA_VOCAB -> if (language == AppLanguage.BANGLA) "$titleBn। $exampleBn।" else "$titleEn. $exampleEn."
        else -> if (language == AppLanguage.BANGLA) "$titleBn। $exampleBn।" else "$titleEn. $exampleEn."
    }
}

data class BanglaPoem(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val traditionBn: String,
    val emoji: String,
    val accentColorHex: Long,
    val artworkKey: String,
    val stanzasBn: List<String>,
    val meaningEn: String,
    val isCompleted: Boolean = false
) {
    fun displayTitle(language: AppLanguage): String =
        if (language == AppLanguage.BANGLA) titleBn else "$titleBn ($titleEn)"
}

data class BundledBanglaAudioStatus(
    val isReady: Boolean = true,
    val verifiedClipsCount: Int = 0,
    val totalClipsCount: Int = 0,
    val localeLabel: String = "বাংলা (bn-BD Offline Audio)"
)

data class StoryPage(
    val id: Long,
    val storyId: String,
    val pageNumber: Int,
    val textEn: String,
    val textBn: String,
    val sceneAssetKey: String
) {
    fun text(language: AppLanguage): String =
        if (language == AppLanguage.BANGLA) textBn else textEn
}

data class Story(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val moralEn: String,
    val moralBn: String,
    val coverAssetKey: String,
    val ageTag: String,
    val accentColorHex: Long,
    val pages: List<StoryPage> = emptyList(),
    val isCompleted: Boolean = false
) {
    fun title(language: AppLanguage): String =
        if (language == AppLanguage.BANGLA) titleBn else titleEn

    fun subtitle(language: AppLanguage): String =
        if (language == AppLanguage.BANGLA) subtitleBn else subtitleEn

    fun moral(language: AppLanguage): String =
        if (language == AppLanguage.BANGLA) moralBn else moralEn
}

data class GameScore(
    val id: Long,
    val childId: Long,
    val gameType: GameType,
    val score: Int,
    val starsEarned: Int,
    val playedAt: Long
)

data class ModuleProgress(
    val moduleType: ModuleType,
    val completedCount: Int,
    val totalCount: Int
) {
    val fraction: Float
        get() = if (totalCount == 0) 0f else (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
}

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class DailyStreak(
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val lastActiveEpochDay: Long = -1L,
    val lastRewardClaimedEpochDay: Long = -1L
) {
    fun isActiveToday(todayEpochDay: Long = currentLocalEpochDay()): Boolean =
        lastActiveEpochDay == todayEpochDay && currentStreak > 0

    fun effectiveStreak(todayEpochDay: Long = currentLocalEpochDay()): Int = when {
        lastActiveEpochDay == todayEpochDay -> currentStreak.coerceAtLeast(0)
        lastActiveEpochDay == todayEpochDay - 1L -> currentStreak.coerceAtLeast(0)
        else -> 0
    }

    fun canClaimDailyBonus(todayEpochDay: Long = currentLocalEpochDay()): Boolean =
        isActiveToday(todayEpochDay) && lastRewardClaimedEpochDay != todayEpochDay

    fun isBonusClaimedToday(todayEpochDay: Long = currentLocalEpochDay()): Boolean =
        isActiveToday(todayEpochDay) && lastRewardClaimedEpochDay == todayEpochDay

    fun dailyBonusStars(todayEpochDay: Long = currentLocalEpochDay()): Int {
        val streak = effectiveStreak(todayEpochDay).coerceAtLeast(1)
        return (streak * 5).coerceIn(5, 25)
    }
}

fun currentLocalEpochDay(nowMillis: Long = System.currentTimeMillis()): Long {
    val cal = java.util.Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }
    val localMidnightUtc = cal.timeInMillis + cal.timeZone.getOffset(cal.timeInMillis)
    return localMidnightUtc / 86_400_000L
}
