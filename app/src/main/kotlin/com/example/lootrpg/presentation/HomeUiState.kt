package com.example.lootrpg.presentation

import com.example.lootrpg.player.domain.Player

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Loaded(val player: Player, val experience: ExperienceDisplay) : HomeUiState
    data object Error : HomeUiState
}

/** Only the Level 1 display target is specified; this is not a progression formula. */
data class ExperienceDisplay(val current: Long, val required: Long?) {
    val fraction: Float
        get() = required?.takeIf { it > 0 }?.let {
            (current.toDouble() / it).coerceIn(0.0, 1.0).toFloat()
        } ?: 0f

    companion object {
        fun forPlayer(player: Player) = ExperienceDisplay(
            current = player.currentXp,
            required = if (player.level == 1) 100 else null,
        )
    }
}
