package com.example.padelscore.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val PadelColors = Colors(
    primary         = CobaltDark,
    primaryVariant  = CobaltDark,
    secondary       = CobaltLight,
    secondaryVariant = CobaltLight,
    background      = Color.Black,
    surface         = Color(0xFF1C1C1E),
    error           = Color(0xFFCF6679),
    onPrimary       = Color.White,
    onSecondary     = Color.White,
    onBackground    = Color.White,
    onSurface       = Color(0xFF9E9E9E),
    onSurfaceVariant = Color(0xFF757575),
    onError         = Color.White
)

@Composable
fun PadelScoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = PadelColors,
        content = content
    )
}
