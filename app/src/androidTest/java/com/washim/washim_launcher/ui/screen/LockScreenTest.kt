package com.washim.washim_launcher.ui.screen

import androidx.biometric.BiometricManager
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.washim.washim_launcher.ui.theme.LauncherTheme
import com.washim.washim_launcher.viewmodel.LockViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LockScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun lockScreen_showsClockAndUnlockHint() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val vm = LockViewModel(BiometricManager.from(context))
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent { LauncherTheme { LockScreen(vm, {}) } }
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.onNodeWithContentDescription("Analog clock").assertExists()
        composeTestRule.onNodeWithText("swipe up to unlock", ignoreCase = true).assertExists()
    }
}
