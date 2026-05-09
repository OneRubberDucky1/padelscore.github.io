package com.example.padelscore.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class AppTypography(
    val titleSize: TextUnit,
    val labelSize: TextUnit,
    val labelTracking: TextUnit,
    val labelSizeSmall: TextUnit,
    val buttonTextSize: TextUnit,
    val pillNumberSize: TextUnit,
    val scoreSize: TextUnit,
    val setCellNumber: TextUnit,
    val gameScoreSize: TextUnit,
)

val LocalAppTypography = compositionLocalOf<AppTypography> { error("No AppTypography provided") }

// All fractions derived from the 192dp SMALL_ROUND baseline.
@Composable
fun rememberAppTypography(): AppTypography {
    val sw = LocalConfiguration.current.screenWidthDp
    return remember(sw) {
        AppTypography(
            titleSize = (sw * 0.1563f).sp,
            labelSize = (sw * 0.0573f).sp,
            labelTracking = 1.6f.sp,
            labelSizeSmall = (sw * 0.0469f).sp,
            buttonTextSize = (sw * 0.0677f).sp,
            pillNumberSize = (sw * 0.0573f).sp,
            scoreSize = (sw * 0.1563f).sp,
            setCellNumber = (sw * 0.0677f).sp,
            gameScoreSize = (sw * 0.0833f).sp,
        )
    }
}

// ─── Weights (not screen-size dependent) ───────────────────────────────────
val WeightBold     = FontWeight.Bold
val WeightSemibold = FontWeight.SemiBold
val WeightMedium   = FontWeight.Medium
val WeightExtra    = FontWeight.ExtraBold
