package com.example.lootrpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lootrpg.presentation.HomeViewModel
import com.example.lootrpg.ui.HomeScreen
import com.example.lootrpg.ui.ExileHuntTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        val container = (application as LootRpgApplication).container
        val factory = viewModelFactory {
            initializer { HomeViewModel(container.loadPlayer) }
        }
        setContent {
            ExileHuntTheme {
                val model: HomeViewModel = viewModel(factory = factory)
                val state by model.state.collectAsStateWithLifecycle()
                HomeScreen(state = state, onRetry = model::retry)
            }
        }
    }
}
