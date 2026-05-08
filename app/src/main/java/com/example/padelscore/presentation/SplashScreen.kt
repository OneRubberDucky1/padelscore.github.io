package com.example.padelscore.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalDensity
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
fun SplashScreen(onStartGame: (Int) -> Unit) {
    var selectedIndex by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Padel",
            fontSize = TitleSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Best of",
            fontSize = LabelSize,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        MatchFormatSelector(
            selectedIndex = selectedIndex,
            onSelect = { selectedIndex = it }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onStartGame(matchOptions[selectedIndex]) },
            modifier = Modifier.size(StartButtonSize),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary
            )
        ) {
            Text(
                text = "Start",
                fontSize = ButtonTextSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MatchFormatSelector(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val indicatorPosition by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "selector"
    )

    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SelectorPaddingHorizontal)
            .height(SelectorHeight)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colors.surface)
    ) {
        val density = LocalDensity.current
        val totalWidth = with(density) { maxWidth.toPx() }
        val r = with(density) { (maxHeight / 2).toPx() }
        val segmentWidth = totalWidth / matchOptions.size
        val skewPx = r * SelectorSkewFraction
        val strokePx = with(density) { SelectorStroke.toPx() }

        val x = indicatorPosition * segmentWidth
        val leftRound = (1f - (x / r)).coerceIn(0f, 1f)
        val rightRound = ((x + segmentWidth - (totalWidth - r)) / r).coerceIn(0f, 1f)

        val tlx = lerp(x + skewPx, r, leftRound)
        val blx = lerp(x, r, leftRound)
        val trx = lerp(x + segmentWidth, totalWidth - r, rightRound)
        val brx = lerp(x + segmentWidth - skewPx, totalWidth - r, rightRound)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(tlx, 0f)
                lineTo(trx, 0f)

                if (rightRound > 0f) {
                    arcTo(
                        rect = Rect(totalWidth - 2 * r, 0f, totalWidth, 2 * r),
                        startAngleDegrees = 270f,
                        sweepAngleDegrees = 180f,
                        forceMoveTo = false
                    )
                }

                lineTo(brx, size.height)
                lineTo(blx, size.height)

                if (leftRound > 0f) {
                    arcTo(
                        rect = Rect(0f, 0f, 2 * r, 2 * r),
                        startAngleDegrees = 90f,
                        sweepAngleDegrees = 180f,
                        forceMoveTo = false
                    )
                }

                close()
            }

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(GoldLight, GoldDark),
                    start = Offset(x, 0f),
                    end = Offset(x + segmentWidth, size.height)
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
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$value",
                        fontSize = ButtonTextSize,
                        fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Normal,
                        color = if (index == selectedIndex) GoldLight else MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(onStartGame = {})
}
