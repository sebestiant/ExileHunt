package com.example.lootrpg.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.lootrpg.R

@Composable
internal fun InventoryScreen() {
    ScreenColumn {
        Text(stringResource(R.string.inventory), style = MaterialTheme.typography.headlineLarge)
        Panel {
            Icon(painterResource(R.drawable.ic_inventory), contentDescription = null,
                modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.inventory_empty), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.inventory_hint), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
