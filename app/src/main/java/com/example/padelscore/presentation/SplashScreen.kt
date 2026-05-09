package com.example.padelscore.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.presentation.theme.*

private val matchOptions = listOf(1, 3, 5)

@Composable
fun SplashScreen(onStartGame: (Int, Int) -> Unit) {
    val dim = LocalAppDimensions.current
    val typ = LocalAppTypography.current
    var selectedGamesIndex by remember { mutableIntStateOf(1) }
    var selectedSetsIndex by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PADEL",
            fontSize = typ.titleSize,
            fontWeight = WeightExtra,
            color = CobaltLight,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dim.spacingXs))
        Text(
            text = "GAMES",
            fontSize = typ.labelSizeSmall,
            color = OnSurfaceDim,
            letterSpacing = typ.labelTracking
        )
        Spacer(modifier = Modifier.height(dim.spacingXxs))
        MatchFormatSelector(
            selectedIndex = selectedGamesIndex,
            onSelect = { selectedGamesIndex = it }
        )
        Spacer(modifier = Modifier.height(dim.spacingXs))
        Text(
            text = "SETS",
            fontSize = typ.labelSizeSmall,
            color = OnSurfaceDim,
            letterSpacing = typ.labelTracking
        )
        Spacer(modifier = Modifier.height(dim.spacingXxs))
        MatchFormatSelector(
            selectedIndex = selectedSetsIndex,
            onSelect = { selectedSetsIndex = it }
        )
        Spacer(modifier = Modifier.height(dim.spacingS))
        Button(
            onClick = { onStartGame(matchOptions[selectedGamesIndex], matchOptions[selectedSetsIndex]) },
            modifier = Modifier.size(dim.buttonWidth, dim.buttonHeight),
            shape = dim.buttonShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ChipFill
            )
        ) {
            Text(
                text = "Start",
                fontSize = typ.buttonTextSize,
                fontWeight = WeightBold,
                color = ChipText
            )
        }
    }
}

@Composable
fun MatchFormatSelector(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val dim = LocalAppDimensions.current
    val typ = LocalAppTypography.current
    val indicatorPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "selector"
    )

    Box(
        modifier = Modifier
            .width(dim.selectorWidth)
            .height(dim.selectorHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colors.surface)
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalWidth = size.width
            val r = size.height / 2f
            val segmentWidth = totalWidth / matchOptions.size
            val x = indicatorPosition * segmentWidth
            val strokePx = dim.selectorStroke.toPx()

            val path = Path().apply {
                moveTo(x + r, 0f)
                lineTo(x + segmentWidth - r, 0f)
                arcTo(
                    rect = Rect(x + segmentWidth - 2 * r, 0f, x + segmentWidth, 2 * r),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                lineTo(x + r, size.height)
                arcTo(
                    rect = Rect(x, 0f, x + 2 * r, 2 * r),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 180f,
                    forceMoveTo = false
                )
                close()
            }

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(CobaltLight, CobaltDark),
                    start = Offset(x + segmentWidth, 0f),
                    end = Offset(x, size.height)
                ),
                style = Stroke(width = strokePx, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        Row(modifier = Modifier.fillMaxSize()) {
            matchOptions.forEachIndexed { index, value ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$value",
                        fontSize = typ.pillNumberSize,
                        fontWeight = if (index == selectedIndex) WeightBold else WeightMedium,
                        color = if (index == selectedIndex) CobaltLight else OnSurfaceMuted
                    )
                }
            }
        }
    }
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    PadelScoreTheme { SplashScreen(onStartGame = { _, _ -> }) }
}
