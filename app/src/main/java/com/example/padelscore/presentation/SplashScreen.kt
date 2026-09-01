package com.example.padelscore.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.presentation.theme.*

private val matchOptions = listOf(1, 3, 5)

data class TeamColorPair(val left: Color, val right: Color)

private val teamColorPairs = listOf(
    TeamColorPair(CobaltLight, CobaltDark),
    TeamColorPair(TeamRed, TeamGreen),
    TeamColorPair(TeamGold, TeamPurple),
    TeamColorPair(TeamOrange, TeamWhite)
)

@Composable
fun SplashScreen(onStartGame: (Int, Int, TeamColorPair) -> Unit) {
    val dim = LocalAppDimensions.current
    val typ = LocalAppTypography.current
    var selectedGamesIndex by remember { mutableIntStateOf(1) }
    var selectedSetsIndex by remember { mutableIntStateOf(1) }
    var selectedPairIndex by remember { mutableIntStateOf(0) }
    var showColorPicker by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ColorPairPill(
                pair = teamColorPairs[selectedPairIndex],
                onClick = { showColorPicker = true }
            )
            Spacer(modifier = Modifier.height(dim.spacingXs))
            LabeledSelector("GAMES", selectedGamesIndex) { selectedGamesIndex = it }
            Spacer(modifier = Modifier.height(dim.spacingXs))
            LabeledSelector("SETS", selectedSetsIndex) { selectedSetsIndex = it }
            Spacer(modifier = Modifier.height(dim.spacingS))
            Button(
                onClick = { onStartGame(matchOptions[selectedGamesIndex], matchOptions[selectedSetsIndex], teamColorPairs[selectedPairIndex]) },
                modifier = Modifier.size(dim.buttonWidth, dim.buttonHeight),
                shape = dim.buttonShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = ChipFill)
            ) {
                Text(text = "Start", fontSize = typ.buttonTextSize, fontWeight = WeightBold, color = ChipText)
            }
        }

        AnimatedVisibility(
            visible = showColorPicker,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            TeamColorPickerOverlay(
                selectedPairIndex = selectedPairIndex,
                onPairSelected = { selectedPairIndex = it; showColorPicker = false },
                onDismiss = { showColorPicker = false }
            )
        }
    }
}

@Composable
fun TeamColorPickerOverlay(
    selectedPairIndex: Int,
    onPairSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val dim = LocalAppDimensions.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBlack)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dim.spacingS)
        ) {
            teamColorPairs.chunked(2).forEachIndexed { rowIdx, row ->
                Row(horizontalArrangement = Arrangement.spacedBy(dim.spacingS)) {
                    row.forEachIndexed { colIdx, pair ->
                        val index = rowIdx * 2 + colIdx
                        ColorPairPill(
                            pair = pair,
                            isSelected = index == selectedPairIndex,
                            onClick = { onPairSelected(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPairPill(pair: TeamColorPair, isSelected: Boolean = false, onClick: () -> Unit) {
    val dim = LocalAppDimensions.current
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(dim.setCellHeight)
            .height(dim.setCellWidth)
            .drawBehind {
                val corner = CornerRadius(dim.setCellRadius.toPx())
                drawRoundRect(color = SurfaceCard, cornerRadius = corner)
                drawRoundRect(
                    color = if (isSelected) CobaltLine else OnSurface.copy(alpha = 0.08f),
                    cornerRadius = corner,
                    style = Stroke(width = if (isSelected) dim.setCellBorder.toPx() else 1.dp.toPx())
                )
                drawLine(
                    color = SurfaceDivider,
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        shape = RoundedCornerShape(dim.setCellRadius),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
    ) {
        PillDotRow(leftColor = pair.left, rightColor = pair.right)
    }
}

@Composable
fun PillDotRow(leftColor: Color, rightColor: Color) {
    val dim = LocalAppDimensions.current
    Row(modifier = Modifier.fillMaxSize()) {
        listOf(leftColor, rightColor).forEach { color ->
            Box(modifier = Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(dim.spacingM)) { drawCircle(color = color) }
            }
        }
    }
}

@Composable
fun LabeledSelector(label: String, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val dim = LocalAppDimensions.current
    val typ = LocalAppTypography.current
    Text(label, fontSize = typ.labelSizeSmall, color = OnSurfaceDim, letterSpacing = typ.labelTracking)
    Spacer(modifier = Modifier.height(dim.spacingXxs))
    MatchFormatSelector(selectedIndex = selectedIndex, onSelect = onSelect)
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

    Box(modifier = Modifier.width(dim.selectorWidth).height(dim.selectorHeight)) {
        Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(50)).background(MaterialTheme.colors.surface))

        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = size.height / 2f
            val segmentWidth = size.width / matchOptions.size
            val x = indicatorPosition * segmentWidth
            val path = Path().apply {
                moveTo(x + r, 0f)
                lineTo(x + segmentWidth - r, 0f)
                arcTo(Rect(x + segmentWidth - 2 * r, 0f, x + segmentWidth, 2 * r), 270f, 180f, false)
                lineTo(x + r, size.height)
                arcTo(Rect(x, 0f, x + 2 * r, 2 * r), 90f, 180f, false)
                close()
            }
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(CobaltLight, CobaltDark),
                    start = Offset(x + segmentWidth, 0f),
                    end = Offset(x, size.height)
                ),
                style = Stroke(width = dim.selectorStroke.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        Row(modifier = Modifier.fillMaxSize()) {
            matchOptions.forEachIndexed { index, value ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onSelect(index) },
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
    PadelScoreTheme { SplashScreen(onStartGame = { _, _, _ -> }) }
}
