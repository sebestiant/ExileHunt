package com.example.lootrpg.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

/** Decorative original silhouette only; no area models, interaction, or game rules. */
@Composable
internal fun AreaPreview() {
    Box(Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF202C2E))) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color(0xFF9C9A80), size.minDimension * 0.13f, Offset(size.width * 0.78f, size.height * 0.28f))
            drawPath(Path().apply {
                moveTo(0f, size.height * 0.75f)
                lineTo(size.width * 0.24f, size.height * 0.35f)
                lineTo(size.width * 0.48f, size.height * 0.72f)
                lineTo(size.width * 0.67f, size.height * 0.47f)
                lineTo(size.width, size.height * 0.8f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }, Color(0xFF354440))
            drawPath(Path().apply {
                moveTo(0f, size.height * 0.93f)
                lineTo(size.width * 0.38f, size.height * 0.65f)
                lineTo(size.width * 0.8f, size.height * 0.94f)
                lineTo(size.width, size.height * 0.72f)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }, Color(0xFF152422))
        }
    }
}
