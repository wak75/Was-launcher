package com.washim.washim_launcher.ui.screen

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.washim.washim_launcher.data.AppRepository
import com.washim.washim_launcher.data.DockPreferencesDataStore
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.theme.LauncherTheme
import com.washim.washim_launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_showsClockAndDock() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val vm = fakeViewModel(context)
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent { LauncherTheme { HomeScreen(vm) } }
        composeTestRule.mainClock.advanceTimeBy(500)
        composeTestRule.onNodeWithContentDescription("Analog clock").assertExists()
        composeTestRule.onNodeWithTag("dock_bar").assertExists()
    }
}

private fun fakeViewModel(context: Context): LauncherViewModel {
    val repo = object : AppRepository(context, context.packageManager) {
        override fun getInstalledApps(): Flow<List<AppInfo>> = MutableStateFlow(emptyList())
        override fun getWallpaper() = null
    }
    val dock = DockPreferencesDataStore(context)
    return LauncherViewModel(repo, dock)
}
