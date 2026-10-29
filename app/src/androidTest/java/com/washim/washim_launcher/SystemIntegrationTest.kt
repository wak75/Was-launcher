package com.washim.washim_launcher

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SystemIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun mainActivity_edgeToEdge_statusBarTransparent() {
        activityRule.scenario.onActivity { activity ->
            val color = activity.window.statusBarColor
            assertTrue("Status bar should be transparent", color == android.graphics.Color.TRANSPARENT)
        }
    }
}
