package com.example.padelscore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.padelscore.presentation.theme.*
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.R

@Composable
fun ScoreScreen(
    gamesFormat: Int = 3,
    setsFormat: Int = 3,
    onReturnHome: () -> Unit = {},
    viewModel: ScoreViewModel = viewModel(factory = ScoreViewModel.factory(gamesFormat, setsFormat))
) {
    if (viewModel.isMatchOver) {
        GameOverScreen(
            winnerIsLeft = viewModel.matchWinnerIsLeft,
            completedSets = viewModel.completedSets,
            onReturnHome = onReturnHome,
            onUndo = { viewModel.undo() }
        )
    } else {
        val dim = LocalAppDimensions.current
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dim.spacingM, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UtilityButton(onClick = onReturnHome, R.drawable.ic_home)
                UtilityButton(onClick = { viewModel.undo() }, R.drawable.ic_arrow_back)
            }
            Spacer(modifier = Modifier.height(dim.spacingXl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dim.spacingM, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScoreButton(score = viewModel.leftScore, fill = CobaltLight, onClick = { viewModel.incrementLeft() })
                val typ = LocalAppTypography.current
                Text(text = ":", fontSize = typ.scoreSize, fontWeight = WeightBold, color = OnSurface)
                ScoreButton(score = viewModel.rightScore, fill = CobaltDark, onClick = { viewModel.incrementRight() })
            }
            Spacer(modifier = Modifier.height(dim.spacingM))
            SetPillsWithServer(viewModel.setScores, viewModel.currentSetIndex, viewModel.isLeftServing)
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun ScoreScreenPreview() {
    PadelScoreTheme { ScoreScreen() }
}

@Composable
fun UtilityButton(onClick: () -> Unit = {}, icon: Int) {
    val dim = LocalAppDimensions.current
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(dim.topControlBtnWidth)
            .height(dim.topControlBtnHeight)
            .drawBehind {
                val corner = CornerRadius(dim.utilityButtonCorner.toPx())
                drawRoundRect(color = SurfaceRail, cornerRadius = corner)
                drawRoundRect(color = OnSurface.copy(alpha = 0.16f), cornerRadius = corner, style = Stroke(width = 1.dp.toPx()))
            },
        shape = dim.utilityButtonShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = OnSurface,
            modifier = Modifier.size(dim.iconSize)
        )
    }
}

@Composable
fun ScoreButton(score: String, fill: Color, onClick: () -> Unit) {
    val dim = LocalAppDimensions.current
    val typ = LocalAppTypography.current
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(dim.scoreButton, dim.scoreButton)
            .drawBehind {
                val corner = CornerRadius(dim.buttonCorner.toPx())
                drawRoundRect(color = fill, cornerRadius = corner)
            },
        shape = dim.buttonShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
    ) {
        Text(
            text = score,
            fontSize = typ.scoreSize,
            fontWeight = WeightBold,
            color = OnSurface
        )
    }
}

@Composable
fun SetPillsWithServer(sets: List<SetScore>, currentSetIndex: Int, isLeftServing: Boolean) {
    val dim = LocalAppDimensions.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            ImagePill(isLeftServing = isLeftServing)
        }
        Spacer(modifier = Modifier.width(dim.spacingXxs))
        NumberOfScorePill(sets, currentSetIndex)
        Box(modifier = Modifier.weight(1f))
    }
}

@Composable
fun NumberOfScorePill(sets: List<SetScore>, currentSetIndex: Int) {
    val dim = LocalAppDimensions.current
    Row(horizontalArrangement = Arrangement.spacedBy(dim.spacingXxs)) {
        sets.forEachIndexed { index, set ->
            val winner: Boolean? = if (index < currentSetIndex) set.left > set.right else null
            ScorePill(set.left, set.right, isActive = index == currentSetIndex, winner = winner)
        }
    }
}

@Composable
fun ImagePill(isLeftServing: Boolean) {
    val dim = LocalAppDimensions.current
    Box(
        modifier = Modifier
            .width(dim.setCellWidth)
            .height(dim.setCellHeight)
            .drawBehind {
                drawLine(
                    color = SurfaceDivider,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLeftServing) {
                    Icon(
                        painter = painterResource(R.drawable.sport_ball),
                        contentDescription = null,
                        tint = CobaltLight.copy(alpha = 0.65f),
                        modifier = Modifier.size(dim.iconSize)
                    )
                }
            }
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (!isLeftServing) {
                    Icon(
                        painter = painterResource(R.drawable.sport_ball),
                        contentDescription = null,
                        tint = CobaltLight.copy(alpha = 0.65f),
                        modifier = Modifier.size(dim.iconSize)
                    )
                }
            }
        }
    }
}

@Composable
fun ScorePill(left: Int, right: Int, isActive: Boolean, winner: Boolean?) {
    val dim = LocalAppDimensions.current
    val typ = LocalAppTypography.current
    Box(
        modifier = Modifier
            .width(dim.setCellWidth)
            .height(dim.setCellHeight)
            .drawBehind {
                val corner = CornerRadius(dim.setCellRadius.toPx())
                val halfHeight = size.height / 2f
                drawRoundRect(
                    color = if (isActive) OnSurface.copy(alpha = 0.10f) else SurfaceCard,
                    cornerRadius = corner
                )
                if (winner != null) {
                    val pillPath = Path().apply {
                        addRoundRect(RoundRect(Rect(0f, 0f, size.width, size.height), corner))
                    }
                    val topLeft = if (winner) Offset(0f, 0f) else Offset(0f, halfHeight)
                    clipPath(pillPath) {
                        drawRect(
                            brush = Brush.linearGradient(
                                colorStops = arrayOf(
                                    0.0f to CobaltLight.copy(alpha = 0.55f),
                                    0.3f to CobaltLine.copy(alpha = 0.55f),
                                    1.0f to CobaltDark.copy(alpha = 0.55f)
                                ),
                                start = Offset(size.width, topLeft.y),
                                end = Offset(0f, topLeft.y + halfHeight)
                            ),
                            topLeft = topLeft,
                            size = Size(size.width, halfHeight)
                        )
                    }
                }
                if (isActive) {
                    drawRoundRect(
                        color = CobaltLine,
                        cornerRadius = corner,
                        style = Stroke(width = dim.setCellBorder.toPx())
                    )
                } else {
                    drawRoundRect(
                        color = OnSurface.copy(alpha = 0.08f),
                        cornerRadius = corner,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                drawLine(
                    color = SurfaceDivider,
                    start = Offset(0f, halfHeight),
                    end = Offset(size.width, halfHeight),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$left",
                    fontSize = typ.setCellNumber,
                    lineHeight = typ.setCellNumber,
                    fontWeight = WeightBold,
                    color = if (isActive || winner == true) OnSurface else OnSurfaceFaint
                )
            }
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$right",
                    fontSize = typ.setCellNumber,
                    lineHeight = typ.setCellNumber,
                    fontWeight = WeightBold,
                    color = if (isActive || winner == false) OnSurface else OnSurfaceFaint
                )
            }
        }
    }
}
