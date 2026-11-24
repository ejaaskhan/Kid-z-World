package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ChildProfileEntity
import com.example.data.local.entity.GameScoreEntity
import com.example.data.local.entity.LearningProgressEntity
import com.example.data.local.entity.LessonItemEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.StoryPageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildProfileDao {
    @Query("SELECT * FROM child_profiles ORDER BY createdAt ASC")
    fun observeAllProfiles(): Flow<List<ChildProfileEntity>>

    @Query("SELECT COUNT(*) FROM child_profiles")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ChildProfileEntity): Long

    @Query("UPDATE child_profiles SET totalStars = totalStars + :delta WHERE id = :childId")
    suspend fun addStars(childId: Long, delta: Int)

    @Query("UPDATE child_profiles SET totalStars = 0 WHERE id = :childId")
    suspend fun resetStars(childId: Long)

    @Query("DELETE FROM child_profiles WHERE id = :childId")
    suspend fun deleteProfile(childId: Long)
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lesson_items ORDER BY moduleType ASC, sortOrder ASC")
    fun observeAllLessons(): Flow<List<LessonItemEntity>>

    @Query("SELECT COUNT(*) FROM lesson_items")
    suspend fun getLessonCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLessons(items: List<LessonItemEntity>)
}

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories ORDER BY sortOrder ASC")
    fun observeAllStories(): Flow<List<StoryEntity>>

    @Query("SELECT * FROM story_pages ORDER BY storyId ASC, pageNumber ASC")
    fun observeAllStoryPages(): Flow<List<StoryPageEntity>>

    @Query("SELECT COUNT(*) FROM stories")
    suspend fun getStoryCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStoryPages(pages: List<StoryPageEntity>)
}

@Dao
interface ProgressDao {
    @Query("SELECT * FROM learning_progress WHERE childId = :childId")
    fun observeProgressForChild(childId: Long): Flow<List<LearningProgressEntity>>

    @Query("SELECT * FROM learning_progress WHERE childId = :childId AND itemId = :itemId AND itemType = :itemType LIMIT 1")
    suspend fun getProgress(childId: Long, itemId: String, itemType: String): LearningProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: LearningProgressEntity)

    @Query("DELETE FROM learning_progress WHERE childId = :childId")
    suspend fun clearProgressForChild(childId: Long)

    @Query("SELECT * FROM game_scores WHERE childId = :childId ORDER BY playedAt DESC LIMIT 12")
    fun observeScoresForChild(childId: Long): Flow<List<GameScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameScore(score: GameScoreEntity)

    @Query("DELETE FROM game_scores WHERE childId = :childId")
    suspend fun clearScoresForChild(childId: Long)
}
