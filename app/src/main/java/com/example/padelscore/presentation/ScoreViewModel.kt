package com.example.padelscore.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ScoreViewModel : ViewModel() {
    private val scoreArray = arrayOf(0, 15, 30, 40)

    var state by mutableStateOf(GameState())
        private set

    val leftScore: String get() = displayScore(isLeft = true)
    val rightScore: String get() = displayScore(isLeft = false)

    val leftGameScore: Int get() = state.gameScoreLeft
    val rightGameScore: Int get() = state.gameScoreRight

    fun incrementLeft() = handleIncrement(isLeft = true)
    fun incrementRight() = handleIncrement(isLeft = false)

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
            -1 -> "Deuce"
            0 -> if (isLeft) "Adv" else "40"
            1 -> if (!isLeft) "Adv" else "40"
            else -> "Deuce"
        }
    }

    private fun onGameWon(isLeft: Boolean) {
        state = if (isLeft) {
            state.copy(gameScoreLeft = state.gameScoreLeft + 1)
        } else {
            state.copy(gameScoreRight = state.gameScoreRight + 1)
        }
        state = state.copy(counterLeft = 0, counterRight = 0, advantage = -1)
    }
}
