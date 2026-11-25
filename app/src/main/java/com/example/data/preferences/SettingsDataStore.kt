package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.localization.AppLanguage
import com.example.domain.model.AppSettings
import com.example.domain.model.DailyStreak
import com.example.domain.model.currentLocalEpochDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.kidzSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "kidz_world_settings"
)

class SettingsDataStore(context: Context) {

    private val dataStore = context.applicationContext.kidzSettingsDataStore

    private object Keys {
        val LANGUAGE = stringPreferencesKey("app_language")
        val ACTIVE_CHILD_ID = longPreferencesKey("active_child_id")
        val SOUND_FX = booleanPreferencesKey("sound_fx_enabled")
        val TTS_ENABLED = booleanPreferencesKey("tts_enabled")
        val SPEECH_RATE = floatPreferencesKey("speech_rate")
        val DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val settingsFlow: Flow<AppSettings> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            AppSettings(
                language = AppLanguage.fromCode(prefs[Keys.LANGUAGE]),
                activeChildId = prefs[Keys.ACTIVE_CHILD_ID] ?: 1L,
                soundFxEnabled = prefs[Keys.SOUND_FX] ?: true,
                ttsEnabled = prefs[Keys.TTS_ENABLED] ?: true,
                speechRate = prefs[Keys.SPEECH_RATE] ?: 0.84f,
                darkModeEnabled = prefs[Keys.DARK_MODE] ?: false,
                onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false
            )
        }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { it[Keys.LANGUAGE] = language.code }
    }

    suspend fun setActiveChildId(childId: Long) {
        dataStore.edit { it[Keys.ACTIVE_CHILD_ID] = childId }
    }

    suspend fun setSoundFxEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_FX] = enabled }
    }

    suspend fun setTtsEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TTS_ENABLED] = enabled }
    }

    suspend fun setSpeechRate(rate: Float) {
        dataStore.edit { it[Keys.SPEECH_RATE] = rate.coerceIn(0.65f, 1.25f) }
    }

    suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    private fun streakCountKey(childId: Long) = intPreferencesKey("streak_count_$childId")
    private fun streakBestKey(childId: Long) = intPreferencesKey("streak_best_$childId")
    private fun streakLastDayKey(childId: Long) = longPreferencesKey("streak_last_day_$childId")
    private fun streakClaimedDayKey(childId: Long) = longPreferencesKey("streak_claimed_day_$childId")

    fun observeDailyStreak(childId: Long): Flow<DailyStreak> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            DailyStreak(
                currentStreak = prefs[streakCountKey(childId)] ?: 0,
                bestStreak = prefs[streakBestKey(childId)] ?: 0,
                lastActiveEpochDay = prefs[streakLastDayKey(childId)] ?: -1L,
                lastRewardClaimedEpochDay = prefs[streakClaimedDayKey(childId)] ?: -1L
            )
        }

    /**
     * Records a learning activity for today in DataStore.
     * Returns the updated streak count if today's streak was newly incremented/started, or null if already active today.
     */
    suspend fun recordLearningActivity(
        childId: Long,
        todayEpochDay: Long = currentLocalEpochDay()
    ): Int? {
        var newlyLitStreak: Int? = null
        dataStore.edit { prefs ->
            val lastDay = prefs[streakLastDayKey(childId)] ?: -1L
            val current = prefs[streakCountKey(childId)] ?: 0
            val best = prefs[streakBestKey(childId)] ?: 0

            if (lastDay == todayEpochDay && current > 0) {
                return@edit
            }

            val updatedStreak = if (lastDay == todayEpochDay - 1L && current > 0) {
                current + 1
            } else {
                1
            }
            val updatedBest = maxOf(best, updatedStreak)

            prefs[streakCountKey(childId)] = updatedStreak
            prefs[streakBestKey(childId)] = updatedBest
            prefs[streakLastDayKey(childId)] = todayEpochDay
            newlyLitStreak = updatedStreak
        }
        return newlyLitStreak
    }

    /**
     * Claims today's daily streak bonus stars if the child learned today and hasn't claimed yet.
     * Returns stars to award (> 0) or 0 if already claimed / not active today.
     */
    suspend fun claimDailyStreakBonus(
        childId: Long,
        todayEpochDay: Long = currentLocalEpochDay()
    ): Int {
        var bonusAwarded = 0
        dataStore.edit { prefs ->
            val current = prefs[streakCountKey(childId)] ?: 0
            val lastDay = prefs[streakLastDayKey(childId)] ?: -1L
            val claimedDay = prefs[streakClaimedDayKey(childId)] ?: -1L

            if (lastDay == todayEpochDay && current > 0 && claimedDay != todayEpochDay) {
                bonusAwarded = (current * 5).coerceIn(5, 25)
                prefs[streakClaimedDayKey(childId)] = todayEpochDay
            }
        }
        return bonusAwarded
    }

    suspend fun clearStreak(childId: Long) {
        dataStore.edit { prefs ->
            prefs.remove(streakCountKey(childId))
            prefs.remove(streakBestKey(childId))
            prefs.remove(streakLastDayKey(childId))
            prefs.remove(streakClaimedDayKey(childId))
        }
    }
}
