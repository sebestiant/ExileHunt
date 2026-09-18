package com.example.lootrpg.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lootrpg.R
import com.example.lootrpg.presentation.EncounterPhase
import com.example.lootrpg.presentation.HomeUiState
import java.util.Locale

@Composable
internal fun HuntScreen(state: HomeUiState.Loaded, onHunt: () -> Unit, onAcknowledgeOpening: () -> Unit) {
    if (!state.player.introductionAcknowledged) {
        ScreenColumn {
            SectionHeading(stringResource(R.string.the_fracture))
            Text(stringResource(R.string.opening_lore), style = MaterialTheme.typography.bodyLarge)
            Panel {
                AreaPreview()
                Text(state.area.name, style = MaterialTheme.typography.headlineSmall)
                Text(stringResource(R.string.recommended_start))
                Text(state.area.description)
            }
            if (state.actionError != null) Text(stringResource(R.string.intro_save_error), color = MaterialTheme.colorScheme.error)
            Button(onClick = onAcknowledgeOpening, enabled = !state.busy,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                Text(stringResource(R.string.beyond_walls))
            }
        }
        return
    }
    val resultScroll = rememberScrollState()
    LaunchedEffect(state.phase) {
        if (state.phase == EncounterPhase.Searching) resultScroll.scrollTo(0)
    }
    Column(Modifier.fillMaxSize()) {
        // Keep the core action in view while result/history scroll beneath it.
        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(state.player.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(stringResource(R.string.level_value, state.player.level))
            }
            Text(stringResource(R.string.gold_value, state.player.gold), color = MaterialTheme.colorScheme.primary)
            ExperienceBar(state.experience)
            Button(onClick = onHunt, enabled = state.huntAvailable,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                Text(when {
                    state.remainingSeconds > 0 -> stringResource(R.string.next_hunt,
                        String.format(Locale.ROOT, "%02d:%02d", state.remainingSeconds / 60, state.remainingSeconds % 60))
                    state.busy -> stringResource(R.string.resolving)
                    else -> stringResource(R.string.hunt_action)
                }, style = MaterialTheme.typography.titleMedium)
            }
            if (state.huntAvailable) Text(stringResource(R.string.hunt_available), style = MaterialTheme.typography.labelMedium)
            if (state.actionError != null) Text(stringResource(R.string.hunt_error), color = MaterialTheme.colorScheme.error)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Box(Modifier.weight(1f)) {
            ScreenColumn(scrollState = resultScroll) {
                val latest = state.snapshot.recentHunts.firstOrNull()
                when (state.phase) {
                    EncounterPhase.Searching -> Panel { Text(stringResource(R.string.searching, state.area.name)) }
                    EncounterPhase.Encounter -> if (latest != null) Panel {
                        SectionHeading(stringResource(R.string.encounter))
                        MonsterEmblem(latest.monsterId)
                        Text(latest.monsterName, style = MaterialTheme.typography.headlineSmall)
                        Text(stringResource(R.string.level_value, latest.monsterLevel))
                    }
                    EncounterPhase.Idle -> if (latest != null) HuntResultCard(latest)
                }
                Panel {
                    SectionHeading(stringResource(R.string.current_area))
                    Text(state.area.name, style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.recommended_levels, state.area.recommendedLevels.first, state.area.recommendedLevels.last))
                    if (latest == null) AreaPreview()
                    Text(state.area.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                RecentHunts(state.snapshot.recentHunts)
            }
        }
    }
}
