package com.example.di

import android.content.Context
import com.example.core.audio.KidzAudioEngine
import com.example.data.local.KidzWorldDatabase
import com.example.data.preferences.SettingsDataStore
import com.example.data.repository.KidzWorldRepository

class AppContainer(context: Context) {
    private val appContext = context.applicationContext

    val database: KidzWorldDatabase by lazy {
        KidzWorldDatabase.getInstance(appContext)
    }

    val settingsStore: SettingsDataStore by lazy {
        SettingsDataStore(appContext)
    }

    val repository: KidzWorldRepository by lazy {
        KidzWorldRepository(database, settingsStore)
    }

    val audioEngine: KidzAudioEngine by lazy {
        KidzAudioEngine(appContext)
    }
}
