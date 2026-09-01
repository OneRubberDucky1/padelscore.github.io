package com.example.padelscore.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.presentation.theme.CobaltDark
import com.example.padelscore.presentation.theme.CobaltLight
import com.example.padelscore.presentation.theme.PadelScoreTheme
import com.example.padelscore.presentation.theme.TeamColor

private enum class Screen { SPLASH, GAME }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            PadelScoreApp()
        }
    }
}

@Composable
fun PadelScoreApp() {
    PadelScoreTheme {
        val activity = LocalActivity.current as ComponentActivity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            var screen by remember { mutableStateOf(Screen.SPLASH) }
            var gamesFormat by remember { mutableStateOf(3) }
            var setsFormat by remember { mutableStateOf(3) }
            var teamColors by remember {
                mutableStateOf(TeamColorPair(TeamColor("Cobalt Light", CobaltLight), TeamColor("Cobalt Dark", CobaltDark)))
            }
            when (screen) {
                Screen.SPLASH -> SplashScreen(onStartGame = { games, sets, colors ->
                    gamesFormat = games
                    setsFormat = sets
                    teamColors = colors
                    screen = Screen.GAME
                })
                Screen.GAME -> ScoreScreen(
                    gamesFormat = gamesFormat,
                    setsFormat = setsFormat,
                    leftTeam = teamColors.left,
                    rightTeam = teamColors.right,
                    onReturnHome = {
                        activity.viewModelStore.clear()
                        screen = Screen.SPLASH
                    }
                )
            }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Preview(device = WearDevices.LARGE_ROUND, showSystemUi = true)
@Composable
fun PadelScoreAppPreview() {
    PadelScoreApp()
}
