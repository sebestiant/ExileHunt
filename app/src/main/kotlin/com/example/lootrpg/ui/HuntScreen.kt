package com.example.lootrpg.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lootrpg.R
import com.example.lootrpg.presentation.HomeUiState

@Composable
internal fun HuntScreen(state: HomeUiState.Loaded, onHuntPreview: () -> Unit) {
    ScreenColumn {
        SectionHeading(stringResource(R.string.your_journey))
        PlayerSummary(state)
        Panel {
            SectionHeading(stringResource(R.string.current_area))
            AreaPreview()
            Text(stringResource(R.string.area_outskirts), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.area_description), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(stringResource(R.string.hunt_available), color = MaterialTheme.colorScheme.secondary)
        Button(onClick = onHuntPreview, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
            Text(stringResource(R.string.hunt), style = MaterialTheme.typography.titleMedium)
        }
        Text(
            stringResource(R.string.hunting_coming_soon),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
