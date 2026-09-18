package com.example.lootrpg.ui

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.lootrpg.R
import com.example.lootrpg.presentation.HomeUiState

private val equipmentLabels = listOf(R.string.weapon, R.string.helmet, R.string.armour, R.string.gloves, R.string.boots)

@Composable
internal fun CharacterScreen(state: HomeUiState.Loaded) {
    ScreenColumn {
        Text(stringResource(R.string.character), style = MaterialTheme.typography.headlineLarge)
        PlayerSummary(state)
        Panel {
            SectionHeading(stringResource(R.string.hunt_statistics))
            DetailRow(stringResource(R.string.total_hunts), state.player.totalHunts.toString())
            DetailRow(stringResource(R.string.victories), state.player.victories.toString())
            DetailRow(stringResource(R.string.defeats), state.player.defeats.toString())
        }
        Panel {
            SectionHeading(stringResource(R.string.combat_stats))
            DetailRow(stringResource(R.string.attack), state.player.stats.attack.toString())
            DetailRow(stringResource(R.string.defense), state.player.stats.defense.toString())
            DetailRow(stringResource(R.string.health), state.player.stats.maxHealth.toString())
        }
        Panel {
            SectionHeading(stringResource(R.string.equipment))
            equipmentLabels.forEachIndexed { index, label ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                DetailRow(stringResource(label), stringResource(R.string.empty))
            }
        }
    }
}
