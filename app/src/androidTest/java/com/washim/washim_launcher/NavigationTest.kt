package com.washim.washim_launcher

import androidx.biometric.BiometricManager
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.washim.washim_launcher.data.AppRepository
import com.washim.washim_launcher.data.DockPreferencesDataStore
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.LauncherNavHost
import com.washim.washim_launcher.ui.theme.LauncherTheme
import com.washim.washim_launcher.viewmodel.LauncherScreen
import com.washim.washim_launcher.viewmodel.LauncherViewModel
import com.washim.washim_launcher.viewmodel.LockViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun navigateToAppMenu_swipeLeftFromHome() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Build fake ViewModels using the same pattern as HomeScreenTest / AppMenuScreenTest
        val repo = object : AppRepository(context, context.packageManager) {
            override fun getInstalledApps() = MutableStateFlow(emptyList<AppInfo>())
            override fun getWallpaper() = null
        }
        val launcherViewModel = LauncherViewModel(repo, DockPreferencesDataStore(context))
        val lockViewModel = LockViewModel(BiometricManager.from(context))

        // Unlock immediately so NavHost navigates from LOCK → HOME
        lockViewModel.onAuthenticated()

        // Pause auto-advance so infinite animations don't block compose from going idle
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.setContent {
            LauncherTheme {
                LauncherNavHost(lockViewModel, launcherViewModel)
            }
        }

        // Advance compose clock enough for initial navigation to HOME to settle
        composeTestRule.mainClock.advanceTimeBy(1_000)

        // Navigate to APP_MENU via ViewModel (simulates the swipe-left gesture result)
        launcherViewModel.navigate(LauncherScreen.APP_MENU)

        // Advance compose clock enough for navigation transition to complete
        composeTestRule.mainClock.advanceTimeBy(1_000)

        composeTestRule.onNodeWithTag("app_menu_root").assertIsDisplayed()
    }
}
