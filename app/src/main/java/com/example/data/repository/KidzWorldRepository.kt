package com.example.data.repository

import com.example.core.localization.AppLanguage
import com.example.data.local.BanglaPoemsCatalog
import com.example.data.local.KidzWorldDatabase
import com.example.data.local.SeedContentProvider
import com.example.data.local.entity.ChildProfileEntity
import com.example.data.local.entity.GameScoreEntity
import com.example.data.local.entity.LearningProgressEntity
import com.example.data.preferences.SettingsDataStore
import com.example.domain.model.AppSettings
import com.example.domain.model.BanglaPoem
import com.example.domain.model.ChildProfile
import com.example.domain.model.DailyStreak
import com.example.domain.model.GameScore
import com.example.domain.model.GameType
import com.example.domain.model.LessonItem
import com.example.domain.model.ModuleType
import com.example.domain.model.Story
import com.example.domain.model.StoryPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
class KidzWorldRepository(
    private val database: KidzWorldDatabase,
    private val settingsStore: SettingsDataStore
) {
    private val profileDao = database.childProfileDao()
    private val lessonDao = database.lessonDao()
    private val storyDao = database.storyDao()
    private val progressDao = database.progressDao()

    val settingsFlow: Flow<AppSettings> = settingsStore.settingsFlow

    val profilesFlow: Flow<List<ChildProfile>> = profileDao.observeAllProfiles().map { list ->
        list.map { entity ->
            ChildProfile(
                id = entity.id,
                name = entity.name,
                age = entity.age,
                avatarKey = entity.avatarKey,
                totalStars = entity.totalStars,
                createdAt = entity.createdAt
            )
        }
    }

    fun observeLessonsForChild(childId: Long): Flow<List<LessonItem>> = combine(
        lessonDao.observeAllLessons(),
        progressDao.observeProgressForChild(childId)
    ) { lessons, progressList ->
        val completedIds = progressList
            .filter { it.itemType == ITEM_TYPE_LESSON && it.isCompleted }
            .map { it.itemId }
            .toSet()

        lessons.map { entity ->
            LessonItem(
                id = entity.id,
                moduleType = ModuleType.fromKey(entity.moduleType),
                symbol = entity.symbol,
                symbolBn = entity.symbolBn,
                titleEn = entity.titleEn,
                titleBn = entity.titleBn,
                phoneticEn = entity.phoneticEn,
                phoneticBn = entity.phoneticBn,
                exampleEn = entity.exampleEn,
                exampleBn = entity.exampleBn,
                visualBadge = entity.visualBadge,
                accentColorHex = entity.accentColorHex,
                countValue = entity.countValue,
                sortOrder = entity.sortOrder,
                isLearned = entity.id in completedIds
            )
        }
    }

    fun observeStoriesForChild(childId: Long): Flow<List<Story>> = combine(
        storyDao.observeAllStories(),
        storyDao.observeAllStoryPages(),
        progressDao.observeProgressForChild(childId)
    ) { stories, pages, progressList ->
        val pagesByStory = pages.groupBy { it.storyId }
        val completedStories = progressList
            .filter { it.itemType == ITEM_TYPE_STORY && it.isCompleted }
            .map { it.itemId }
            .toSet()

        stories.map { s ->
            Story(
                id = s.id,
                titleEn = s.titleEn,
                titleBn = s.titleBn,
                subtitleEn = s.subtitleEn,
                subtitleBn = s.subtitleBn,
                moralEn = s.moralEn,
                moralBn = s.moralBn,
                coverAssetKey = s.coverAssetKey,
                ageTag = s.ageTag,
                accentColorHex = s.accentColorHex,
                pages = pagesByStory[s.id].orEmpty().map { p ->
                    StoryPage(
                        id = p.id,
                        storyId = p.storyId,
                        pageNumber = p.pageNumber,
                        textEn = p.textEn,
                        textBn = p.textBn,
                        sceneAssetKey = p.sceneAssetKey
                    )
                },
                isCompleted = s.id in completedStories
            )
        }
    }

    fun observeScoresForChild(childId: Long): Flow<List<GameScore>> =
        progressDao.observeScoresForChild(childId).map { list ->
            list.map { e ->
                GameScore(
                    id = e.id,
                    childId = e.childId,
                    gameType = GameType.fromKey(e.gameType),
                    score = e.score,
                    starsEarned = e.starsEarned,
                    playedAt = e.playedAt
                )
            }
        }

    fun observePoemsForChild(childId: Long): Flow<List<BanglaPoem>> =
        progressDao.observeProgressForChild(childId).map { progressList ->
            val completedIds = progressList
                .filter { it.itemType == ITEM_TYPE_POEM && it.isCompleted }
                .map { it.itemId }
                .toSet()
            BanglaPoemsCatalog.poems.map { poem ->
                poem.copy(isCompleted = poem.id in completedIds)
            }
        }

    val activeLessonsFlow: Flow<List<LessonItem>> = settingsFlow
        .map { it.activeChildId }
        .flatMapLatest { childId -> observeLessonsForChild(childId) }

    val activeStoriesFlow: Flow<List<Story>> = settingsFlow
        .map { it.activeChildId }
        .flatMapLatest { childId -> observeStoriesForChild(childId) }

    val activePoemsFlow: Flow<List<BanglaPoem>> = settingsFlow
        .map { it.activeChildId }
        .flatMapLatest { childId -> observePoemsForChild(childId) }

    val activeScoresFlow: Flow<List<GameScore>> = settingsFlow
        .map { it.activeChildId }
        .flatMapLatest { childId -> observeScoresForChild(childId) }

    val activeDailyStreakFlow: Flow<DailyStreak> = settingsFlow
        .map { it.activeChildId }
        .flatMapLatest { childId -> settingsStore.observeDailyStreak(childId) }

    suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        if (profileDao.getProfileCount() == 0) {
            val firstId = profileDao.insertProfile(
                ChildProfileEntity(
                    name = "Ayaan",
                    age = 5,
                    avatarKey = "owl",
                    totalStars = 15
                )
            )
            profileDao.insertProfile(
                ChildProfileEntity(
                    name = "Zara",
                    age = 7,
                    avatarKey = "rabbit",
                    totalStars = 25
                )
            )
            settingsStore.setActiveChildId(firstId)
        }

        val allSeedLessons = SeedContentProvider.buildAllLessons()
        if (lessonDao.getLessonCount() < allSeedLessons.size) {
            lessonDao.insertAllLessons(allSeedLessons)
        }

        if (storyDao.getStoryCount() == 0) {
            val (stories, pages) = SeedContentProvider.buildStories()
            storyDao.insertStories(stories)
            storyDao.insertStoryPages(pages)
        }
    }

    suspend fun addChildProfile(name: String, age: Int, avatarKey: String): Long =
        withContext(Dispatchers.IO) {
            val newId = profileDao.insertProfile(
                ChildProfileEntity(
                    name = name.trim(),
                    age = age.coerceIn(3, 8),
                    avatarKey = avatarKey,
                    totalStars = 0
                )
            )
            settingsStore.setActiveChildId(newId)
            newId
        }

    suspend fun deleteChildProfile(childId: Long, fallbackChildId: Long) =
        withContext(Dispatchers.IO) {
            progressDao.clearProgressForChild(childId)
            progressDao.clearScoresForChild(childId)
            settingsStore.clearStreak(childId)
            profileDao.deleteProfile(childId)
            settingsStore.setActiveChildId(fallbackChildId)
        }

    suspend fun markLessonLearned(childId: Long, lessonId: String): Boolean =
        withContext(Dispatchers.IO) {
            val existing = progressDao.getProgress(childId, lessonId, ITEM_TYPE_LESSON)
            settingsStore.recordLearningActivity(childId)
            if (existing?.isCompleted == true) {
                false
            } else {
                progressDao.upsertProgress(
                    LearningProgressEntity(
                        childId = childId,
                        itemId = lessonId,
                        itemType = ITEM_TYPE_LESSON,
                        isCompleted = true,
                        starsEarned = 5
                    )
                )
                profileDao.addStars(childId, 5)
                true
            }
        }

    suspend fun markStoryCompleted(childId: Long, storyId: String): Boolean =
        withContext(Dispatchers.IO) {
            val existing = progressDao.getProgress(childId, storyId, ITEM_TYPE_STORY)
            val newlyCompleted = existing?.isCompleted != true
            settingsStore.recordLearningActivity(childId)
            progressDao.upsertProgress(
                LearningProgressEntity(
                    childId = childId,
                    itemId = storyId,
                    itemType = ITEM_TYPE_STORY,
                    isCompleted = true,
                    starsEarned = 15
                )
            )
            if (newlyCompleted) {
                profileDao.addStars(childId, 15)
            }
            newlyCompleted
        }

    suspend fun markPoemCompleted(childId: Long, poemId: String): Boolean =
        withContext(Dispatchers.IO) {
            val existing = progressDao.getProgress(childId, poemId, ITEM_TYPE_POEM)
            val newlyCompleted = existing?.isCompleted != true
            settingsStore.recordLearningActivity(childId)
            progressDao.upsertProgress(
                LearningProgressEntity(
                    childId = childId,
                    itemId = poemId,
                    itemType = ITEM_TYPE_POEM,
                    isCompleted = true,
                    starsEarned = 15
                )
            )
            if (newlyCompleted) {
                profileDao.addStars(childId, 15)
            }
            newlyCompleted
        }

    suspend fun recordGameScore(childId: Long, gameType: GameType, score: Int, starsEarned: Int) =
        withContext(Dispatchers.IO) {
            settingsStore.recordLearningActivity(childId)
            progressDao.insertGameScore(
                GameScoreEntity(
                    childId = childId,
                    gameType = gameType.key,
                    score = score,
                    starsEarned = starsEarned
                )
            )
            if (starsEarned > 0) {
                profileDao.addStars(childId, starsEarned)
            }
        }

    suspend fun claimDailyStreakBonus(childId: Long): Int = withContext(Dispatchers.IO) {
        val bonus = settingsStore.claimDailyStreakBonus(childId)
        if (bonus > 0) {
            profileDao.addStars(childId, bonus)
        }
        bonus
    }

    suspend fun resetChildProgress(childId: Long) = withContext(Dispatchers.IO) {
        progressDao.clearProgressForChild(childId)
        progressDao.clearScoresForChild(childId)
        settingsStore.clearStreak(childId)
        profileDao.resetStars(childId)
    }

    suspend fun setLanguage(language: AppLanguage) = settingsStore.setLanguage(language)
    suspend fun setActiveChild(childId: Long) = settingsStore.setActiveChildId(childId)
    suspend fun setSoundFxEnabled(enabled: Boolean) = settingsStore.setSoundFxEnabled(enabled)
    suspend fun setTtsEnabled(enabled: Boolean) = settingsStore.setTtsEnabled(enabled)
    suspend fun setSpeechRate(rate: Float) = settingsStore.setSpeechRate(rate)
    suspend fun setDarkModeEnabled(enabled: Boolean) = settingsStore.setDarkModeEnabled(enabled)
    suspend fun setOnboardingCompleted(completed: Boolean) = settingsStore.setOnboardingCompleted(completed)

    companion object {
        private const val ITEM_TYPE_LESSON = "lesson"
        private const val ITEM_TYPE_STORY = "story"
        private const val ITEM_TYPE_POEM = "poem"
    }
}
