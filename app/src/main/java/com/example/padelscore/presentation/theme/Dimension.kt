package com.example.padelscore.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppDimensions(
    val scoreButton: Dp,
    val buttonWidth: Dp,
    val buttonHeight: Dp,
    val selectorWidth: Dp,
    val selectorHeight: Dp,
    val selectorStroke: Dp,
    val topControlBtnWidth: Dp,
    val topControlBtnHeight: Dp,
    val topControlGap: Dp,
    val setCellWidth: Dp,
    val setCellHeight: Dp,
    val setCellRadius: Dp,
    val setCellGap: Dp,
    val setCellBorder: Dp,
    val iconSize: Dp,
    val buttonCorner: Dp,
    val utilityButtonCorner: Dp,
    val spacingXxs: Dp,
    val spacingXs: Dp,
    val spacingS: Dp,
    val spacingM: Dp,
    val spacingL: Dp,
    val spacingXl: Dp,
    val buttonShape: Shape,
    val utilityButtonShape: Shape,
)

val LocalAppDimensions = compositionLocalOf<AppDimensions> { error("No AppDimensions provided") }

// All fractions derived from the 192dp SMALL_ROUND baseline.
@Composable
fun rememberAppDimensions(): AppDimensions {
    val sw = LocalConfiguration.current.screenWidthDp.dp
    return remember(sw) {
        val buttonCorner = sw * 0.0729f
        val utilityButtonCorner = sw * 0.0417f
        AppDimensions(
            scoreButton = sw * 0.3125f,
            buttonWidth = sw * 0.4167f,
            buttonHeight = sw * 0.1458f,
            selectorWidth = sw * 0.625f,
            selectorHeight = sw * 0.1094f,
            selectorStroke = sw * 0.0130f,
            topControlBtnWidth = sw * 0.1458f,
            topControlBtnHeight = sw * 0.1146f,
            topControlGap = sw * 0.0313f,
            setCellWidth = sw * 0.1042f,
            setCellHeight = sw * 0.1875f,
            setCellRadius = sw * 0.0365f,
            setCellGap = sw * 0.0313f,
            setCellBorder = sw * 0.0078f,
            iconSize = sw * 0.0729f,
            buttonCorner = buttonCorner,
            utilityButtonCorner = utilityButtonCorner,
            spacingXxs = sw * 0.0104f,
            spacingXs = sw * 0.0208f,
            spacingS = sw * 0.0313f,
            spacingM = sw * 0.0417f,
            spacingL = sw * 0.0521f,
            spacingXl = sw * 0.0781f,
            buttonShape = RoundedCornerShape(buttonCorner),
            utilityButtonShape = RoundedCornerShape(utilityButtonCorner),
        )
    }
}
