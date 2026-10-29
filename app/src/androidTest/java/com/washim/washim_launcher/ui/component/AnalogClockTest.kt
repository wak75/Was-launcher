package com.washim.washim_launcher.ui.component

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.washim.washim_launcher.ui.theme.LauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalogClockTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun analogClock_renders_withoutCrash() {
        composeTestRule.setContent { LauncherTheme { AnalogClock() } }
        composeTestRule.onRoot().assertExists()
    }
}
