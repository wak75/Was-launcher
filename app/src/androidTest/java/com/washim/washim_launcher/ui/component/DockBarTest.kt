package com.washim.washim_launcher.ui.component

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.theme.LauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DockBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dockBar_renders_fourSlots() {
        val apps = (1..4).map { fakeAppInfo("com.$it", "App$it") }
        composeTestRule.setContent { LauncherTheme { DockBar(apps, {}, { _, _ -> }) } }
        apps.forEach { composeTestRule.onNodeWithContentDescription(it.label).assertExists() }
    }
}

private fun fakeAppInfo(pkg: String, label: String) = AppInfo(
    packageName = pkg,
    label = label,
    icon = ImageBitmap(1, 1),
)
