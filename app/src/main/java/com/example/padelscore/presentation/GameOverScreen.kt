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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.R
import com.example.padelscore.presentation.theme.*

@Composable
fun GameOverScreen(
    winnerIsLeft: Boolean,
    completedSets: List<SetScore>,
    onReturnHome: () -> Unit = {},
    onUndo: () -> Unit = {},
    leftTeam: TeamColor,
    rightTeam: TeamColor
) {
    val dim = LocalAppDimensions.current
    val typ = LocalAppTypography.current
    val leftSets = completedSets.count { it.left > it.right }
    val rightSets = completedSets.count { it.right > it.left }
    val winner = if (winnerIsLeft) leftTeam else rightTeam

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "MATCH OVER",
            fontSize = typ.labelSizeSmall,
            fontWeight = WeightMedium,
            color = OnSurfaceFaint,
            letterSpacing = typ.labelTracking,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dim.spacingXs))
        Text(
            text = "${winner.name.uppercase()} WINS",
            fontSize = typ.gameScoreSize,
            fontWeight = WeightExtra,
            color = winner.color,
            textAlign = TextAlign.Center
        )
        Text(
            text = "$leftSets – $rightSets",
            fontSize = typ.labelSize,
            fontWeight = WeightMedium,
            color = OnSurfaceDim,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(dim.spacingM))
        NumberOfScorePill(
            sets = completedSets,
            currentSetIndex = completedSets.size,
            leftColor = leftTeam.color,
            rightColor = rightTeam.color
        )
        Spacer(modifier = Modifier.height(dim.spacingL))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dim.spacingM, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UtilityButton(onReturnHome, icon = R.drawable.ic_home)
            UtilityButton(onUndo, icon = R.drawable.ic_arrow_back)
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun GameOverScreenPreview() {
    PadelScoreTheme {
        GameOverScreen(
            winnerIsLeft = true,
            completedSets = listOf(SetScore(2, 1), SetScore(1, 2), SetScore(2, 0)),
            leftTeam = TeamColor("Cobalt Light", CobaltLight),
            rightTeam = TeamColor("Cobalt Dark", CobaltDark)
        )
    }
}
