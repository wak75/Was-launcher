package com.washim.washim_launcher.ui.screen

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.washim.washim_launcher.data.AppRepository
import com.washim.washim_launcher.data.DockPreferencesDataStore
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.theme.LauncherTheme
import com.washim.washim_launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecentsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun recentsScreen_swipeCardUp_removesCard() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val testApps = listOf(
            AppInfo("com.spotify.music", "Spotify", ImageBitmap(1, 1)),
            AppInfo("com.android.settings", "Settings", ImageBitmap(1, 1)),
        )
        val repo = object : AppRepository(context, context.packageManager) {
            override fun getInstalledApps() = MutableStateFlow(testApps)
            override fun getWallpaper() = null
        }
        val fakeVm = LauncherViewModel(repo, DockPreferencesDataStore(context))

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent { LauncherTheme { RecentsScreen(fakeVm) } }
        composeTestRule.mainClock.advanceTimeBy(500)
        // Dismiss usage-stats dialog only if it appears (permission may be granted on some devices)
        if (composeTestRule.onAllNodes(androidx.compose.ui.test.hasText("Not now"))
                .fetchSemanticsNodes().isNotEmpty()) {
            composeTestRule.onNodeWithText("Not now").performClick()
        }
        composeTestRule.onNodeWithText("Spotify").assertExists()
        composeTestRule.onNodeWithText("Spotify")
            .performTouchInput { swipeUp() }
        composeTestRule.onNodeWithText("Spotify").assertDoesNotExist()
    }
}
