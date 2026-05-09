package com.example.padelscore.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.presentation.theme.PadelScoreTheme

private enum class Screen { SPLASH, GAME }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            PadelScoreApp()
        }
    }
}

@Composable
fun PadelScoreApp() {
    PadelScoreTheme {
        val activity = LocalContext.current as ComponentActivity
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            var screen by remember { mutableStateOf(Screen.SPLASH) }
            var gamesFormat by remember { mutableStateOf(3) }
            var setsFormat by remember { mutableStateOf(3) }
            when (screen) {
                Screen.SPLASH -> SplashScreen(onStartGame = { games, sets ->
                    gamesFormat = games
                    setsFormat = sets
                    screen = Screen.GAME
                })
                Screen.GAME -> ScoreScreen(
                    gamesFormat = gamesFormat,
                    setsFormat = setsFormat,
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
@Composable
fun PadelScoreAppPreview() {
    PadelScoreApp()
}
