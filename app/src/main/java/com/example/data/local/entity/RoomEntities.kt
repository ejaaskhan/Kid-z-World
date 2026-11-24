package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "child_profiles")
data class ChildProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val age: Int,
    val avatarKey: String,
    val totalStars: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_items")
data class LessonItemEntity(
    @PrimaryKey val id: String,
    val moduleType: String,
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
    val sortOrder: Int = 0
)

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val moralEn: String,
    val moralBn: String,
    val coverAssetKey: String,
    val ageTag: String,
    val accentColorHex: Long,
    val sortOrder: Int = 0
)

@Entity(
    tableName = "story_pages",
    indices = [Index(value = ["storyId"])]
)
data class StoryPageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val storyId: String,
    val pageNumber: Int,
    val textEn: String,
    val textBn: String,
    val sceneAssetKey: String
)

@Entity(
    tableName = "learning_progress",
    primaryKeys = ["childId", "itemId", "itemType"]
)
data class LearningProgressEntity(
    val childId: Long,
    val itemId: String,
    val itemType: String, // "lesson" or "story"
    val isCompleted: Boolean = true,
    val starsEarned: Int = 5,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "game_scores",
    indices = [Index(value = ["childId"])]
)
data class GameScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val childId: Long,
    val gameType: String,
    val score: Int,
    val starsEarned: Int,
    val playedAt: Long = System.currentTimeMillis()
)
