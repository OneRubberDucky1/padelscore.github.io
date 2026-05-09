package com.example.padelscore.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val PadelColors = Colors(
    primary          = CobaltLight,
    primaryVariant   = CobaltDark,
    secondary        = ChipFill,
    secondaryVariant = ChipFill,
    background       = SurfaceBlack,
    surface          = SurfaceRail,
    error            = Color(0xFFFF6B6B),
    onPrimary        = ChipText,
    onSecondary      = ChipText,
    onBackground     = OnSurface,
    onSurface        = OnSurface,
    onSurfaceVariant = OnSurfaceMuted,
    onError          = OnSurface
)

@Composable
fun PadelScoreTheme(content: @Composable () -> Unit) {
    val dimensions = rememberAppDimensions()
    val typography = rememberAppTypography()
    CompositionLocalProvider(
        LocalAppDimensions provides dimensions,
        LocalAppTypography provides typography,
    ) {
        MaterialTheme(colors = PadelColors, content = content)
    }
}
