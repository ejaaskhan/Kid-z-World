package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChildProfileDao
import com.example.data.local.dao.LessonDao
import com.example.data.local.dao.ProgressDao
import com.example.data.local.dao.StoryDao
import com.example.data.local.entity.ChildProfileEntity
import com.example.data.local.entity.GameScoreEntity
import com.example.data.local.entity.LearningProgressEntity
import com.example.data.local.entity.LessonItemEntity
import com.example.data.local.entity.StoryEntity
import com.example.data.local.entity.StoryPageEntity

@Database(
    entities = [
        ChildProfileEntity::class,
        LessonItemEntity::class,
        StoryEntity::class,
        StoryPageEntity::class,
        LearningProgressEntity::class,
        GameScoreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KidzWorldDatabase : RoomDatabase() {
    abstract fun childProfileDao(): ChildProfileDao
    abstract fun lessonDao(): LessonDao
    abstract fun storyDao(): StoryDao
    abstract fun progressDao(): ProgressDao

    companion object {
        @Volatile
        private var INSTANCE: KidzWorldDatabase? = null

        fun getInstance(context: Context): KidzWorldDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KidzWorldDatabase::class.java,
                    "kidz_world_offline.db"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
