package com.example.padelscore.presentation

data class GameState(
    val counterLeft: Int = 0,
    val counterRight: Int = 0,
    val gameScoreLeft: Int = 0,
    val gameScoreRight: Int = 0,
    val setScoreLeft: Int = 0,
    val setScoreRight: Int = 0,
    val advantage: Int = -1,
    val isLeftServing: Boolean = true,
    val completedSets: List<SetScore> = emptyList(),
    val currentSetIndex: Int = 0
)

data class SetScore(val left: Int, val right: Int)