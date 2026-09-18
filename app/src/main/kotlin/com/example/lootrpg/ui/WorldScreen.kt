package com.example.lootrpg.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.lootrpg.R

private val futureLocationLabels = listOf(R.string.area_woods, R.string.area_ruins, R.string.area_pass)

@Composable
internal fun WorldScreen() {
    ScreenColumn {
        Text(stringResource(R.string.world), style = MaterialTheme.typography.headlineLarge)
        Panel {
            AreaPreview()
            Text(stringResource(R.string.area_outskirts), style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.area_current), color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.area_description), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SectionHeading(stringResource(R.string.beyond_frontier))
        futureLocationLabels.forEach { label ->
            Panel { DetailRow(stringResource(label), stringResource(R.string.locked)) }
        }
    }
}
