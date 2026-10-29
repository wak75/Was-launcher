package com.washim.washim_launcher.ui.screen

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit4.runners.AndroidJUnit4
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
class AppMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun appMenuScreen_searchFiltersResults() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val testApps = listOf(
            AppInfo("com.spotify.music", "Spotify", ImageBitmap(1, 1)),
            AppInfo("com.android.settings", "Settings", ImageBitmap(1, 1)),
        )
        val repo = object : AppRepository(context, context.packageManager) {
            override fun getInstalledApps() = MutableStateFlow(testApps)
            override fun getWallpaper() = null
        }
        val vm = LauncherViewModel(repo, DockPreferencesDataStore(context))
        composeTestRule.setContent { LauncherTheme { AppMenuScreen(vm) } }
        composeTestRule.onNodeWithTag("search_input").performTextInput("spot")
        composeTestRule.onNodeWithText("Spotify").assertExists()
        composeTestRule.onNodeWithText("Settings").assertDoesNotExist()
    }
}
