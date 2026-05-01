package com.example.padelscore.presentation

data class GameState(
    val counterLeft: Int = 0,
    val counterRight: Int = 0,
    val advantage: Int = -1
)
