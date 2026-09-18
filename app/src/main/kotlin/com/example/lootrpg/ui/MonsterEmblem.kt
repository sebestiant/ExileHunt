package com.example.lootrpg.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Distinct typographic tokens; disposable placeholders, not final artwork. */
@Composable
internal fun MonsterEmblem(monsterId: String) {
    val (mark, color) = when (monsterId) {
        "rotted-hound" -> "RH" to Color(0xFFABC392)
        "thorn-rat" -> "TR" to Color(0xFFCCBC86)
        "roadside-marauder" -> "RM" to Color(0xFFB9BFC9)
        "hollow-wanderer" -> "HW" to Color(0xFF9BC9D0)
        "fractured-boar" -> "FB" to Color(0xFFD49E8B)
        else -> "?" to MaterialTheme.colorScheme.onSurface
    }
    Surface(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, color),
        color = MaterialTheme.colorScheme.surfaceVariant) {
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Text(mark, color = color, style = MaterialTheme.typography.titleMedium)
        }
    }
}
