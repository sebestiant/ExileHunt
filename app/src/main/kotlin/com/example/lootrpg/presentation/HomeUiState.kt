package com.example.lootrpg.presentation

import com.example.lootrpg.player.domain.Player
import com.example.lootrpg.player.domain.Progression
import com.example.lootrpg.hunt.domain.HuntState
import com.example.lootrpg.hunt.domain.GameDefinitions

enum class EncounterPhase { Idle, Searching, Encounter }
enum class HuntActionError { Hunt, Introduction }

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Loaded(
        val snapshot: HuntState,
        val remainingSeconds: Long = 0,
        val busy: Boolean = false,
        val phase: EncounterPhase = EncounterPhase.Idle,
        val actionError: HuntActionError? = null,
    ) : HomeUiState {
        val player get() = snapshot.player
        val experience get() = ExperienceDisplay.forPlayer(player)
        val area get() = GameDefinitions.wildOutskirts
        val huntAvailable get() = player.introductionAcknowledged && !busy && remainingSeconds == 0L
    }
    data object Error : HomeUiState
}

/** UI formatting delegates requirements to Domain. */
data class ExperienceDisplay(val current: Long, val required: Long?) {
    val fraction: Float
        get() = required?.takeIf { it > 0 }?.let {
            (current.toDouble() / it).coerceIn(0.0, 1.0).toFloat()
        } ?: 0f

    companion object {
        fun forPlayer(player: Player) = ExperienceDisplay(
            current = player.currentXp,
            required = Progression.requiredXp(player.level),
        )
    }
}
