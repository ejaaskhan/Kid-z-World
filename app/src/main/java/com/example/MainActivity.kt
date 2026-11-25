package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.di.AppContainer
import com.example.ui.screens.KidzWorldApp
import com.example.ui.viewmodel.KidzWorldViewModel

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer
    private lateinit var viewModel: KidzWorldViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.decorView.isSoundEffectsEnabled = false

        appContainer = AppContainer(applicationContext)
        viewModel = ViewModelProvider(
            this,
            KidzWorldViewModel.Factory(
                repository = appContainer.repository,
                audioEngine = appContainer.audioEngine
            )
        )[KidzWorldViewModel::class.java]

        setContent {
            KidzWorldApp(viewModel = viewModel)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::appContainer.isInitialized) {
            appContainer.audioEngine.stopSpeaking()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::appContainer.isInitialized) {
            appContainer.audioEngine.release()
        }
    }
}
