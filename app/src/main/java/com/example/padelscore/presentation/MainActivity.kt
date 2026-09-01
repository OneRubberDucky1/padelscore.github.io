package com.example.padelscore.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.padelscore.presentation.theme.PadelScoreTheme

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
fun PadelScoreApp(appViewModel: AppViewModel = viewModel()) {
    PadelScoreTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            when (appViewModel.screen) {
                Screen.SPLASH -> SplashScreen(onStartGame = appViewModel::startGame)
                Screen.GAME -> ScoreScreen(
                    gamesFormat = appViewModel.gamesFormat,
                    setsFormat = appViewModel.setsFormat,
                    matchId = appViewModel.matchId,
                    leftTeam = appViewModel.teamColors.left,
                    rightTeam = appViewModel.teamColors.right,
                    onReturnHome = appViewModel::returnHome
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
