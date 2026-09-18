package com.example.lootrpg.presentation

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Ready : HomeUiState
    data object Error : HomeUiState
}
