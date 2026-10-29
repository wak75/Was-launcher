package com.washim.washim_launcher.ui.component

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.theme.LauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZoomScaleListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun zoomScaleList_showsAllItems() {
        val apps = listOf(fakeAppInfo("com.a", "Alpha"), fakeAppInfo("com.b", "Beta"))
        composeTestRule.setContent { LauncherTheme { ZoomScaleList(apps, onAppClick = {}) } }
        composeTestRule.onNodeWithText("Alpha").assertExists()
        composeTestRule.onNodeWithText("Beta").assertExists()
    }
}

private fun fakeAppInfo(pkg: String, label: String) = AppInfo(
    packageName = pkg,
    label = label,
    icon = ImageBitmap(1, 1),
)
