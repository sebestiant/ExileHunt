package com.example.lootrpg.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.lootrpg.R
import com.example.lootrpg.hunt.domain.GameDefinitions

private val futureLocationLabels = listOf(R.string.area_woods, R.string.area_ruins, R.string.area_pass)

@Composable
internal fun WorldScreen() {
    val area = GameDefinitions.wildOutskirts
    ScreenColumn {
        Text(stringResource(R.string.world), style = MaterialTheme.typography.headlineLarge)
        Panel {
            AreaPreview()
            Text(area.name, style = MaterialTheme.typography.headlineSmall)
            Text(stringResource(R.string.area_current), color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.recommended_levels, area.recommendedLevels.first, area.recommendedLevels.last))
            Text(area.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SectionHeading(stringResource(R.string.beyond_frontier))
        futureLocationLabels.forEach { label ->
            Panel { DetailRow(stringResource(label), stringResource(R.string.locked)) }
        }
    }
}
