# KID'Z WORLD — ProGuard / R8 Keep Rules (by Dev. E)
# Note: isMinifyEnabled is kept false for v1.0.0 until verified on physical devices.
# These rules ensure safe future opt-in to R8 shrinking without breaking Room, DataStore, or Compose.

-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve Room entities, DAOs, and generated database implementations
-keep class com.example.data.local.entity.** { *; }
-keep class com.example.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Preserve Kotlin metadata and coroutines stack traces
-keep class kotlin.Metadata { *; }
-dontwarn kotlinx.coroutines.**

# Preserve Jetpack Compose runtime & Navigation state classes
-keep class com.example.domain.model.** { *; }
