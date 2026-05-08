package com.example.padelscore.presentation

data class GameState(
    val counterLeft: Int = 0,
    val counterRight: Int = 0,
    val gameScoreLeft: Int = 0,
    val gameScoreRight: Int = 0,
    val advantage: Int = -1,
    val isLeftServing: Boolean = true
)
