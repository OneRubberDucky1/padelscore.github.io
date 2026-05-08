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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.presentation.theme.*

private val matchOptions = listOf(1, 3, 5)

@Composable
fun SplashScreen(onStartGame: (Int, Int) -> Unit) {
    var selectedGamesIndex by remember { mutableIntStateOf(1) }
    var selectedSetsIndex by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Padel",
            fontSize = TitleSize,
            fontWeight = WeightExtra,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "GAMES",
            fontSize = LabelSize,
            color = OnSurfaceDim,
            letterSpacing = LabelTracking
        )
        Spacer(modifier = Modifier.height(4.dp))
        MatchFormatSelector(
            selectedIndex = selectedGamesIndex,
            onSelect = { selectedGamesIndex = it }
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "SETS",
            fontSize = LabelSize,
            color = OnSurfaceDim,
            letterSpacing = LabelTracking
        )
        Spacer(modifier = Modifier.height(4.dp))
        MatchFormatSelector(
            selectedIndex = selectedSetsIndex,
            onSelect = { selectedSetsIndex = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onStartGame(matchOptions[selectedGamesIndex], matchOptions[selectedSetsIndex]) },
            modifier = Modifier.size(ButtonWidth, ButtonHeight),
            shape = ButtonShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ChipFill
            )
        ) {
            Text(
                text = "Start",
                fontSize = ButtonTextSize,
                fontWeight = FontWeight.Bold,
                color = ChipText
            )
        }
    }
}

@Composable
fun MatchFormatSelector(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val indicatorPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "selector"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SelectorPaddingHorizontal)
            .height(SelectorHeight)
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
            val strokePx = SelectorStroke.toPx()

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
                        fontSize = ButtonTextSize,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                        color = if (index == selectedIndex) CobaltLight else OnSurfaceMuted
                    )
                }
            }
        }
    }
}


@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onStartGame = { _, _ -> })
}
