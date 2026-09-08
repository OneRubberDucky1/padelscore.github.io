package com.example.padelscore.presentation

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

/**
 * Instrumented end-to-end tests driving [MainActivity] — full navigation flow from splash
 * through an in-progress match to the game-over screen.
 */
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_launchesOnSplashScreen() {
        TODO("Not yet implemented")
    }

    @Test
    fun startingMatch_navigatesFromSplashToScoreScreen() {
        TODO("Not yet implemented")
    }

    @Test
    fun completingMatch_navigatesToGameOverScreen() {
        TODO("Not yet implemented")
    }
}
