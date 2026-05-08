package com.example.padelscore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.padelscore.presentation.theme.*
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices

@Composable
fun ScoreScreen(matchFormat: Int = 3, viewModel: ScoreViewModel = viewModel(factory = ScoreViewModel.factory(matchFormat))) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreButton(score = viewModel.leftScore, isServing = viewModel.isLeftServing, onClick = { viewModel.incrementLeft() })
            Text(
                text = ":",
                fontSize = ScoreSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
            )
            ScoreButton(score = viewModel.rightScore, isServing = !viewModel.isLeftServing, onClick = { viewModel.incrementRight() })
        }
        Spacer(modifier = Modifier.height(8.dp))
        GameTracker(leftGames = viewModel.leftGameScore, rightGames = viewModel.rightGameScore)
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun ScoreScreenPreview() {
    ScoreScreen()
}

@Composable
fun ScoreButton(score: String, isServing: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(scoreButton, scoreButton)
            .drawBehind {
                val corner = CornerRadius(10.dp.toPx())
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
                    drawRoundRect(color = CobaltLight, cornerRadius = corner)
                }
            },
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score,
                fontSize = ScoreSize,
                fontWeight = FontWeight.Bold,
                color = if (isServing) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun GameTracker(leftGames: Int, rightGames: Int) {
    Text(
        text = "$leftGames - $rightGames",
        fontSize = GameScoreSize,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colors.onBackground
    )
}
