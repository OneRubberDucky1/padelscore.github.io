package io.github.padelscore.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import io.github.padelscore.presentation.theme.CobaltDark
import io.github.padelscore.presentation.theme.CobaltLight
import io.github.padelscore.presentation.theme.TeamColor

enum class Screen { SPLASH, GAME }

class AppViewModel : ViewModel() {
    var screen by mutableStateOf(Screen.SPLASH)
        private set
    var gamesFormat by mutableIntStateOf(3)
        private set
    var setsFormat by mutableIntStateOf(3)
        private set
    var teamColors by mutableStateOf(
        TeamColorPair(TeamColor("Cobalt Light", CobaltLight), TeamColor("Cobalt Dark", CobaltDark))
    )
        private set
    var matchId by mutableIntStateOf(0)
        private set

    fun startGame(games: Int, sets: Int, colors: TeamColorPair) {
        gamesFormat = games
        setsFormat = sets
        teamColors = colors
        matchId++
        screen = Screen.GAME
    }

    fun returnHome() {
        screen = Screen.SPLASH
    }
}
