package com.example.padelscore.presentation

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

data class GameState(
    val counterLeft: Int = 0,
    val counterRight: Int = 0,
    val gameScoreLeft: Int = 0,
    val gameScoreRight: Int = 0,
    val setScoreLeft: Int = 0,
    val setScoreRight: Int = 0,
    val advantage: Int = -1,
    val isLeftServing: Boolean = true,
    val leftTeam: Boolean = true,
    val completedSets: PersistentList<SetScore> = persistentListOf(),
    val currentSetIndex: Int = 0,
    val isMatchOver: Boolean = false
)

data class SetScore(val left: Int, val right: Int){
    val winnerIsLeft: Boolean get () = left > right
}