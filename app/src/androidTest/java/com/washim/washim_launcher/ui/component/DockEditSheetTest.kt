package com.washim.washim_launcher.ui.component

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.theme.LauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DockEditSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dockEditSheet_selectsApp_callsOnSelect() {
        var selected = ""
        composeTestRule.setContent {
            LauncherTheme {
                DockEditSheet(
                    slot = 0,
                    apps = listOf(fakeAppInfo("com.x", "X")),
                    onSelect = { selected = it },
                    onDismiss = {},
                )
            }
        }
        composeTestRule.onNodeWithText("X").performClick()
        assertEquals("com.x", selected)
    }
}

private fun fakeAppInfo(pkg: String, label: String): AppInfo {
    val bmp = ImageBitmap(1, 1)
    return AppInfo(packageName = pkg, label = label, icon = bmp)
}
