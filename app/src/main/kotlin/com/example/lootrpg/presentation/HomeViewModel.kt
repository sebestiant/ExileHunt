package com.example.lootrpg.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lootrpg.player.domain.LoadPlayerUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadPlayer: LoadPlayerUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = mutableState.asStateFlow()
    private var initialization: Job? = null

    init { initialize() }

    fun retry() {
        if (mutableState.value == HomeUiState.Error) initialize()
    }

    private fun initialize() {
        if (initialization?.isActive == true) return
        initialization = viewModelScope.launch {
            mutableState.value = HomeUiState.Loading
            try {
                val player = loadPlayer()
                mutableState.value = HomeUiState.Loaded(player, ExperienceDisplay.forPlayer(player))
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = HomeUiState.Error
            }
        }
    }
}
