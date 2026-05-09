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
    viewModel: ScoreViewModel = viewModel(factory = ScoreViewModel.factory(gamesFormat, setsFormat))
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UtilityButton(onClick = {}, R.drawable.ic_home)
            UtilityButton(onClick = {}, R.drawable.ic_arrow_back)
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreButton(score = viewModel.leftScore, isServing = viewModel.isLeftServing, onClick = { viewModel.incrementLeft() })
            Text(text = ":", fontSize = ScoreSize, fontWeight = WeightBold, color = OnSurface)
            ScoreButton(score = viewModel.rightScore, isServing = !viewModel.isLeftServing, onClick = { viewModel.incrementRight() })
        }
        Spacer(modifier = Modifier.height(8.dp))
        NumberOfScorePill(viewModel.setScores, viewModel.currentSetIndex)
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ScoreScreenPreview() {
    ScoreScreen()
}

@Composable
fun UtilityButton(onClick: () -> Unit = {}, icon: Int) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(TopControlBtnWidth)
            .height(TopControlBtnHeight)
            .drawBehind {
                val corner = CornerRadius(8.dp.toPx())
                drawRoundRect(color = SurfaceRail, cornerRadius = corner)
                drawRoundRect(color = OnSurface.copy(alpha = 0.16f), cornerRadius = corner, style = Stroke(width = 1.dp.toPx()))
            },
        shape = UtilityButtonShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = OnSurface,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
fun ScoreButton(score: String, isServing: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(scoreButton, scoreButton)
            .drawBehind {
                val corner = CornerRadius(14.dp.toPx())
                if (isServing) {
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colorStops = arrayOf(
                                0.0f to CobaltLight,
                                0.3f to CobaltLine,
                                1.0f to CobaltDark
                            ),
                            start = Offset(size.width, 0f),
                            end = Offset(0f, size.height)
                        ),
                        cornerRadius = corner
                    )
                } else {
                    drawRoundRect(color = ChipFill, cornerRadius = corner)
                }
            },
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
    ) {
        Text(
            text = score,
            fontSize = ScoreSize,
            fontWeight = WeightBold,
            color = if (isServing) OnSurface else ChipText
        )
    }
}

@Composable
fun NumberOfScorePill(sets: List<SetScore>, currentSetIndex: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(SetCellGap)) {
        sets.forEachIndexed { index, set ->
            val winner: Boolean? = if (index < currentSetIndex) set.left > set.right else null
            ScorePill(set.left, set.right, isActive = index == currentSetIndex, winner = winner)
        }
    }
}

@Composable
fun ScorePill(left: Int, right: Int, isActive: Boolean, winner: Boolean?) {
    Box(
        modifier = Modifier
            .width(SetCellWidth)
            .height(SetCellHeight)
            .drawBehind {
                val corner = CornerRadius(SetCellRadius.toPx())
                val halfHeight = size.height / 2f
                // Background fill
                drawRoundRect(
                    color = if (isActive) OnSurface.copy(alpha = 0.10f) else SurfaceCard,
                    cornerRadius = corner
                )
                // Winner gradient fill
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
                // Border
                if (isActive) {
                    drawRoundRect(
                        color = CobaltLine,
                        cornerRadius = corner,
                        style = Stroke(width = SetCellBorder.toPx())
                    )
                } else {
                    drawRoundRect(
                        color = OnSurface.copy(alpha = 0.08f),
                        cornerRadius = corner,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                // Divider
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
                    fontSize = SetCellNumber,
                    lineHeight = SetCellNumber,
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
                    fontSize = SetCellNumber,
                    lineHeight = SetCellNumber,
                    fontWeight = WeightBold,
                    color = if (isActive || winner == false) OnSurface else OnSurfaceFaint
                )
            }
        }
    }
}
