package com.example.lootrpg.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lootrpg.R
import com.example.lootrpg.presentation.HomeUiState

private enum class Destination(val label: Int, val icon: Int) {
    Hunt(R.string.hunt, R.drawable.ic_hunt),
    Character(R.string.character, R.drawable.ic_character),
    Inventory(R.string.inventory, R.drawable.ic_inventory),
    World(R.string.world, R.drawable.ic_world),
}

@Composable
fun HomeScreen(state: HomeUiState, onRetry: () -> Unit, onHunt: () -> Unit, onAcknowledgeOpening: () -> Unit) {
    var selectedName by rememberSaveable { mutableStateOf(Destination.Hunt.name) }
    val selected = Destination.entries.firstOrNull { it.name == selectedName } ?: Destination.Hunt
    val savedScreens = rememberSaveableStateHolder()

    BackHandler(enabled = selected != Destination.Hunt) { selectedName = Destination.Hunt.name }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = selected == destination,
                        onClick = { selectedName = destination.name },
                        icon = { Icon(painterResource(destination.icon), contentDescription = null) },
                        label = { Text(stringResource(destination.label)) },
                    )
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Text(
                stringResource(R.string.app_name), modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary,
            )
            Box(Modifier.weight(1f)) {
                savedScreens.SaveableStateProvider(selected.name) {
                    when (selected) {
                        Destination.Inventory -> InventoryScreen()
                        Destination.World -> WorldScreen()
                        Destination.Hunt, Destination.Character -> when (state) {
                            HomeUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                            HomeUiState.Error -> ScreenColumn {
                                Text(stringResource(R.string.initialization_failed))
                                TextButton(onClick = onRetry) { Text(stringResource(R.string.retry)) }
                            }
                            is HomeUiState.Loaded -> if (selected == Destination.Hunt) {
                                HuntScreen(state, onHunt, onAcknowledgeOpening)
                            } else CharacterScreen(state)
                        }
                    }
                }
            }
        }
    }
}
