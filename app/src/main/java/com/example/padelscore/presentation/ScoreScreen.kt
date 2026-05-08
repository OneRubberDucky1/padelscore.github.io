package com.example.padelscore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun ScoreScreen(viewModel: ScoreViewModel = viewModel()) {
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScoreButton(score = viewModel.leftScore, onClick = { viewModel.incrementLeft() })
            ScoreButton(score = viewModel.rightScore, onClick = { viewModel.incrementRight() })
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
fun ScoreButton(score: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(ScoreButtonSize),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.primary
        )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score,
                fontSize = ScoreSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary
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
