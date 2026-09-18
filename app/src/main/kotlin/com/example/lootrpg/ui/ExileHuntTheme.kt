package com.example.lootrpg.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

private val colors = darkColorScheme(
    primary = Color(0xFFD5B77A),
    onPrimary = Color(0xFF251D10),
    primaryContainer = Color(0xFF403621),
    onPrimaryContainer = Color(0xFFF0D9A5),
    secondary = Color(0xFFA7B9A8),
    secondaryContainer = Color(0xFF403621),
    onSecondaryContainer = Color(0xFFF0D9A5),
    background = Color(0xFF101416),
    onBackground = Color(0xFFE9E4DB),
    surface = Color(0xFF171D20),
    onSurface = Color(0xFFE9E4DB),
    surfaceVariant = Color(0xFF242C2E),
    onSurfaceVariant = Color(0xFFB6BDB8),
    outline = Color(0xFF626A65),
    outlineVariant = Color(0xFF343D3D),
)

private val baseTypography = Typography()
private val typography = baseTypography.copy(
    headlineLarge = baseTypography.headlineLarge.copy(fontFamily = FontFamily.Serif),
    headlineMedium = baseTypography.headlineMedium.copy(fontFamily = FontFamily.Serif),
    headlineSmall = baseTypography.headlineSmall.copy(fontFamily = FontFamily.Serif),
    titleLarge = baseTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
)

@Composable
fun ExileHuntTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, typography = typography, content = content)
}
