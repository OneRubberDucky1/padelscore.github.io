package io.github.padelscore.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [ScoreViewModel] scoring/state-transition logic (deuce, advantage, game/set/match
 * win, undo). Runs as a local JVM test since ScoreViewModel has no Android framework dependency.
 */
class ScoreViewModelTest {
    private lateinit var viewModel: ScoreViewModel

    private fun createViewModel(games: Int = 3, sets: Int = 3) = ScoreViewModel(games, sets)

    private fun ScoreViewModel.scorePoints(left: Int = 0, right: Int = 0) {
        repeat(left) { incrementLeft() }
        repeat(right) { incrementRight() }
    }

    private fun ScoreViewModel.winGames(left: Int = 0, right: Int = 0) {
        repeat(left) { scorePoints(left = 4) }
        repeat(right) { scorePoints(right = 4) }
    }

    @Before
    fun setUp() {
        viewModel = createViewModel()
    }

    @Test
    fun `initial score is love all with left serving`() {
        assertEquals("0", viewModel.leftScore)
        assertEquals("0", viewModel.rightScore)
        assertTrue("left should be serving at game start", viewModel.isLeftServing)
    }

    @Test
    fun `winning a point below game point advances the score`() {
        viewModel.incrementLeft()

        assertEquals("15", viewModel.leftScore)
        assertEquals("0", viewModel.rightScore)
    }

    @Test
    fun `both players reaching 40 shows deuce`() {
        viewModel.scorePoints(left = 3, right = 2)

        viewModel.incrementRight()

        assertEquals("D", viewModel.leftScore)
        assertEquals("D", viewModel.rightScore)
    }

    @Test
    fun `winning a point at deuce grants advantage`() {
        viewModel.scorePoints(left = 3, right = 3)

        viewModel.incrementLeft()

        assertEquals("AD", viewModel.leftScore)
        assertEquals("40", viewModel.rightScore)
    }

    @Test
    fun `losing a point at advantage returns to deuce`() {
        viewModel.scorePoints(left = 3, right = 3)
        viewModel.incrementRight()

        viewModel.incrementLeft()

        assertEquals("D", viewModel.leftScore)
        assertEquals("D", viewModel.rightScore)
    }

    @Test
    fun `winning a point at advantage wins the game`() {
        viewModel.scorePoints(left = 3, right = 3)
        viewModel.incrementRight()

        viewModel.incrementRight()

        assertEquals(1, viewModel.state.gameScoreRight)
        assertEquals(0, viewModel.state.gameScoreLeft)
    }

    @Test
    fun `the opponent can take advantage after deuce is restored`() {
        viewModel.scorePoints(left = 3, right = 3)
        viewModel.incrementRight()
        viewModel.incrementLeft()

        viewModel.incrementLeft()

        assertEquals("AD", viewModel.leftScore)
        assertEquals("40", viewModel.rightScore)
    }

    @Test
    fun `winning a point at 40 wins the game`() {
        viewModel.scorePoints(left = 3, right = 2)

        viewModel.incrementLeft()

        assertEquals(1, viewModel.state.gameScoreLeft)
        assertEquals(0, viewModel.state.gameScoreRight)
    }

    @Test
    fun `winning a game resets the points to love`() {
        viewModel.scorePoints(left = 3, right = 2)

        viewModel.incrementLeft()

        assertEquals("0", viewModel.leftScore)
        assertEquals("0", viewModel.rightScore)
    }

    @Test
    fun `winning a game switches the server`() {
        viewModel.scorePoints(left = 3, right = 2)

        viewModel.incrementLeft()

        assertFalse("left should not be serving when game is over", viewModel.isLeftServing)
    }

    @Test
    fun `winning the required games wins the set and records the set score`() {
        viewModel.winGames(left = 2)

        assertEquals(1, viewModel.state.setScoreLeft)
        assertEquals(0, viewModel.state.setScoreRight)
        assertEquals(listOf(SetScore(2, 0)), viewModel.completedSets)
    }

    @Test
    fun `winning a set resets the game score and advances the set index`() {
        viewModel.winGames(left = 2)

        assertEquals(0, viewModel.state.gameScoreLeft)
        assertEquals(0, viewModel.state.gameScoreRight)
        assertEquals(1, viewModel.currentSetIndex)
    }

    @Test
    fun `a set is won at 2-1 without requiring a two game lead`() {
        viewModel.winGames(left = 1)
        viewModel.winGames(right = 1)

        viewModel.winGames(left = 1)

        assertEquals(1, viewModel.state.setScoreLeft)
        assertEquals(listOf(SetScore(2, 1)), viewModel.completedSets)
    }

    @Test
    fun `winning the required sets ends the match`() {
        val shortMatch = createViewModel(games = 1, sets = 1)
        shortMatch.scorePoints(left = 3, right = 2)

        shortMatch.incrementLeft()

        assertTrue("the match should be over", shortMatch.isMatchOver)
        assertTrue("left should have won", shortMatch.matchWinnerIsLeft)
    }

    @Test
    fun `the right team can win the match`() {
        val shortMatch = createViewModel(games = 1, sets = 1)
        shortMatch.scorePoints(right = 3)

        shortMatch.incrementRight()

        assertTrue("the match should be over", shortMatch.isMatchOver)
        assertFalse("right should have won", shortMatch.matchWinnerIsLeft)
    }

    @Test
    fun `undo reverts the last point`() {
        viewModel.scorePoints(left = 1, right = 1)
        viewModel.incrementLeft()

        viewModel.undo()

        assertEquals("15", viewModel.leftScore)
        assertEquals("15", viewModel.rightScore)
    }

    @Test
    fun `undo with no history does nothing`() {
        viewModel.undo()

        assertEquals("0", viewModel.leftScore)
        assertEquals("0", viewModel.rightScore)
    }

    @Test
    fun `undo restores the previous set across a set boundary`() {
        viewModel.winGames(left = 2)
        assertEquals(listOf(SetScore(2, 0)), viewModel.completedSets)

        viewModel.undo()

        assertEquals(emptyList<SetScore>(), viewModel.completedSets)
        assertEquals(0, viewModel.state.setScoreLeft)
        assertEquals(0, viewModel.currentSetIndex)
        assertEquals(1, viewModel.state.gameScoreLeft)
    }

    @Test
    fun `the view model does not block scoring after the match is over`() {
        val shortMatch = createViewModel(games = 1, sets = 1)
        shortMatch.scorePoints(left = 4)
        assertTrue("arrange: the match should be over", shortMatch.isMatchOver)

        shortMatch.incrementRight()

        assertEquals("15", shortMatch.rightScore)
        assertTrue("the match should stay over", shortMatch.isMatchOver)
    }

    @Test
    fun `setScores pads the unplayed sets for the chosen format`() {
        val scores = viewModel.setScores.value

        assertEquals(listOf(SetScore(0, 0), SetScore(0, 0), SetScore(0, 0)), scores)
    }

    @Test
    fun `setScores shows completed sets alongside the set in progress`() {
        // Read once up front so a stale cached derived value would be caught below.
        assertEquals(3, viewModel.setScores.value.size)

        viewModel.winGames(left = 2) // closes the first set 2-0
        viewModel.winGames(right = 1) // one game into the second set

        assertEquals(
            listOf(SetScore(2, 0), SetScore(0, 1), SetScore(0, 0)),
            viewModel.setScores.value
        )
    }
}
