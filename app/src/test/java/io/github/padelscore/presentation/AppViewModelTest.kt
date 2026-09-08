package io.github.padelscore.presentation

import io.github.padelscore.presentation.theme.TeamColor
import io.github.padelscore.presentation.theme.TeamGreen
import io.github.padelscore.presentation.theme.TeamRed
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [AppViewModel] navigation and session state.
 */
class AppViewModelTest {

    private lateinit var viewModel: AppViewModel
    private val altColors = TeamColorPair(
        TeamColor("Red", TeamRed),
        TeamColor("Green", TeamGreen)
    )

    @Before
    fun setUp() {
        viewModel = AppViewModel()
    }

    @Test
    fun `initial state is splash screen with default formats`() {
        assertEquals(Screen.SPLASH, viewModel.screen)
        assertEquals(3, viewModel.gamesFormat)
        assertEquals(3, viewModel.setsFormat)
        assertEquals(0, viewModel.matchId)
    }

    @Test
    fun `startGame moves screen to GAME and stores format and colors`() {
        viewModel.startGame(5, 1, altColors)

        assertEquals(Screen.GAME, viewModel.screen)
        assertEquals(5, viewModel.gamesFormat)
        assertEquals(1, viewModel.setsFormat)
        assertEquals(altColors, viewModel.teamColors)
    }

    @Test
    fun `startGame increments matchId on each call`() {
        viewModel.startGame(5, 1, altColors)
        assertEquals(1, viewModel.matchId)

        viewModel.startGame(5, 1, altColors)
        assertEquals(2, viewModel.matchId)
    }

    @Test
    fun `returnHome moves screen back to SPLASH`() {
        viewModel.startGame(5, 1, altColors)

        viewModel.returnHome()
        assertEquals(Screen.SPLASH, viewModel.screen)
    }

    @Test
    fun `returnHome preserves the chosen match settings`() {
        viewModel.startGame(5, 1, altColors)

        viewModel.returnHome()
        assertEquals(5, viewModel.gamesFormat)
        assertEquals(1, viewModel.setsFormat)
        assertEquals(altColors, viewModel.teamColors)
        assertEquals(1, viewModel.matchId)
    }
}
