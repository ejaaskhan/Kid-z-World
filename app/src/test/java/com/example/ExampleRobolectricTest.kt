package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.KidzWorldDatabase
import com.example.data.local.SeedContentProvider
import com.example.data.local.entity.ChildProfileEntity
import com.example.data.local.entity.GameScoreEntity
import com.example.data.local.entity.LearningProgressEntity
import com.example.domain.model.GameType
import com.example.domain.model.ModuleType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Kid'z World", appName)
    }

    @Test
    fun `room database seeds all 4 educational modules and persists local progress and quiz scores`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, KidzWorldDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        try {
            val allSeeded = SeedContentProvider.buildAllLessons()
            db.lessonDao().insertAllLessons(allSeeded)

            val savedCount = db.lessonDao().getLessonCount()
            assertEquals(allSeeded.size, savedCount)

            val savedLessons = db.lessonDao().observeAllLessons().first()
            assertEquals(100, savedLessons.count { it.moduleType == ModuleType.NUMBERS.key })
            assertEquals(26, savedLessons.count { it.moduleType == ModuleType.ALPHABET_EN.key })

            // Create child profile and persist lesson progress + module quiz score
            val childId = db.childProfileDao().insertProfile(
                ChildProfileEntity(name = "Ayaan", age = 6, avatarKey = "lion", totalStars = 0)
            )
            db.progressDao().upsertProgress(
                LearningProgressEntity(childId = childId, itemId = "num_100", itemType = "lesson", isCompleted = true, starsEarned = 5)
            )
            db.progressDao().upsertProgress(
                LearningProgressEntity(childId = childId, itemId = "lang_sent_1", itemType = "lesson", isCompleted = true, starsEarned = 5)
            )
            db.progressDao().insertGameScore(
                GameScoreEntity(childId = childId, gameType = GameType.MATH_QUIZ.key, score = 100, starsEarned = 10)
            )

            val progressList = db.progressDao().observeProgressForChild(childId).first()
            val learnedIds = progressList.filter { it.itemType == "lesson" && it.isCompleted }.map { it.itemId }
            assertTrue("num_100" in learnedIds)
            assertTrue("lang_sent_1" in learnedIds)
            assertEquals(2, learnedIds.size)

            val scores = db.progressDao().observeScoresForChild(childId).first()
            assertEquals(1, scores.size)
            assertEquals(GameType.MATH_QUIZ.key, scores.first().gameType)
            assertEquals(100, scores.first().score)
        } finally {
            db.close()
        }
    }
}
