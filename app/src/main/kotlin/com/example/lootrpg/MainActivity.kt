package com.example.lootrpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lootrpg.presentation.HomeViewModel
import com.example.lootrpg.ui.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as LootRpgApplication).container
        val factory = viewModelFactory {
            initializer { HomeViewModel(container.loadPlayer) }
        }
        setContent {
            MaterialTheme {
                val model: HomeViewModel = viewModel(factory = factory)
                val state by model.state.collectAsStateWithLifecycle()
                HomeScreen(state = state, onRetry = model::retry)
            }
        }
    }
}
