package com.example.padelscore.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.R
import com.example.padelscore.presentation.theme.*

@Composable
fun GameOverScreen(
    winnerIsLeft: Boolean,
    completedSets: List<SetScore>,
    onReturnHome: () -> Unit = {},
    onUndo: () -> Unit = {}
) {
    val leftSets = completedSets.count { it.left > it.right }
    val rightSets = completedSets.count { it.right > it.left }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MATCH OVER",
            fontSize = LabelSizeSmall,
            fontWeight = WeightMedium,
            color = OnSurfaceFaint,
            letterSpacing = LabelTracking,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (winnerIsLeft) "LEFT WINS" else "RIGHT WINS",
            fontSize = GameScoreSize,
            fontWeight = WeightExtra,
            color = CobaltLight,
            textAlign = TextAlign.Center
        )
        Text(
            text = "$leftSets – $rightSets",
            fontSize = LabelSize,
            fontWeight = WeightMedium,
            color = OnSurfaceDim,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        NumberOfScorePill(
            sets = completedSets,
            currentSetIndex = completedSets.size
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UtilityButton(onReturnHome, icon = R.drawable.ic_home)
            UtilityButton(onUndo, icon = R.drawable.ic_arrow_back)
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun GameOverScreenPreview() {
    GameOverScreen(
        winnerIsLeft = true,
        completedSets = listOf(SetScore(2, 1), SetScore(1, 2), SetScore(2, 0))
    )
}