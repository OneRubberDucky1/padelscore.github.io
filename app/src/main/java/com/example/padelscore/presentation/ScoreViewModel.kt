package com.example.padelscore.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

class ScoreViewModel(val gamesFormat: Int, val setsFormat: Int) : ViewModel() {

    companion object {
        fun factory(gamesFormat: Int, setsFormat: Int) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ScoreViewModel(gamesFormat, setsFormat) as T
        }
    }

    private val history = mutableListOf<GameState>()

    private val scoreArray = arrayOf(0, 15, 30, 40)

    var state by mutableStateOf(GameState())
        private set

    val leftScore: String get() = displayScore(true)
    val rightScore: String get() = displayScore(false)

    val isLeftServing: Boolean get() = state.isLeftServing
    val isMatchOver: Boolean get() = state.isMatchOver
    val matchWinnerIsLeft: Boolean get() = state.setScoreLeft > state.setScoreRight
    val completedSets: ImmutableList<SetScore> get() = state.completedSets
    val setScores: State<ImmutableList<SetScore>> by lazy {
        derivedStateOf {
            buildList {
                addAll(state.completedSets)
                add(SetScore(state.gameScoreLeft, state.gameScoreRight))
                repeat(setsFormat - size) { add(SetScore(0, 0)) }
            }.toImmutableList()
        }
    }
    val currentSetIndex: Int get() = state.currentSetIndex

    fun incrementLeft() {
        history.add(state)
        handleIncrement(true)
    }
    fun incrementRight() {
        history.add(state)
        handleIncrement(false)
    }

    fun undo() {
        if (history.isNotEmpty()) {
            state = history.removeAt(history.lastIndex)
        }
    }

    private fun handleIncrement(isLeft: Boolean) {
        val playerCounter = if (isLeft) state.counterLeft else state.counterRight
        val oppCounter = if (isLeft) state.counterRight else state.counterLeft
        val inDeuce = playerCounter == scoreArray.lastIndex && oppCounter == scoreArray.lastIndex

        state = if (!inDeuce) {
            if (playerCounter < scoreArray.lastIndex)
                if (isLeft) state.copy(counterLeft = state.counterLeft + 1)
                else state.copy(counterRight = state.counterRight + 1)
            else {
                onGameWon(isLeft)
                state
            }
        } else {
            when (state.advantage) {
                -1 -> state.copy(advantage = if (isLeft) 0 else 1)
                0 -> if (isLeft) { onGameWon(true); state } else state.copy(advantage = -1)
                1 -> if (!isLeft) { onGameWon(false); state } else state.copy(advantage = -1)
                else -> state
            }
        }
    }

    private fun displayScore(isLeft: Boolean): String {
        val playerCounter = if (isLeft) state.counterLeft else state.counterRight
        val oppCounter = if (isLeft) state.counterRight else state.counterLeft
        val inDeuce = playerCounter == scoreArray.lastIndex && oppCounter == scoreArray.lastIndex
        if (!inDeuce) return scoreArray[playerCounter].toString()
        return when (state.advantage) {
            -1 -> "D"
            0 -> if (isLeft) "AD" else "40"
            1 -> if (!isLeft) "AD" else "40"
            else -> "D"
        }
    }

    private fun onGameWon(isLeft: Boolean) {
        state = if (isLeft)
            state.copy(gameScoreLeft = state.gameScoreLeft + 1, isLeftServing = !state.isLeftServing)
        else
            state.copy(gameScoreRight = state.gameScoreRight + 1, isLeftServing = !state.isLeftServing)
        checkSetWin(isLeft)
        resetPoints()
    }

    private fun checkSetWin(isLeft: Boolean) {
        val gamesToWin = (gamesFormat + 1) / 2
        val hasWon = if (isLeft) state.gameScoreLeft >= gamesToWin
                     else state.gameScoreRight >= gamesToWin
        if (hasWon) {
            val finished = SetScore(state.gameScoreLeft, state.gameScoreRight)
            state = if (isLeft)
                state.copy(
                    completedSets = state.completedSets.add(finished),
                    currentSetIndex = state.currentSetIndex + 1,
                    setScoreLeft = state.setScoreLeft + 1,
                    gameScoreLeft = 0,
                    gameScoreRight = 0
                )
            else
                state.copy(
                    completedSets = state.completedSets.add(finished),
                    currentSetIndex = state.currentSetIndex + 1,
                    setScoreRight = state.setScoreRight + 1,
                    gameScoreLeft = 0,
                    gameScoreRight = 0
                )
            checkMatchWin(isLeft)
        }
    }

    private fun checkMatchWin(isLeft: Boolean) {
        val setsToWin = (setsFormat + 1) / 2
        val hasWon = if (isLeft) state.setScoreLeft >= setsToWin
                     else state.setScoreRight >= setsToWin
        if (hasWon) {
            state = state.copy(isMatchOver = true)
        }
    }

    private fun resetPoints() {
        state = state.copy(counterLeft = 0, counterRight = 0, advantage = -1)
    }
}
