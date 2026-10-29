package com.washim.washim_launcher.ui.gesture

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwipeDetectorTest {

    @Test
    fun swipeDetector_rightEdgeIgnored_doesNotFireOnLeft() {
        val screenWidth = 360f
        val startX = 10f
        val result = isWithinEdgeDeadZone(startX, screenWidth, deadZoneDp = 24f, density = 1f)
        assertTrue(result)
    }

    @Test
    fun swipeDetector_leftEdgeIgnored_withinDeadZone() {
        val screenWidth = 360f
        // 24dp dead zone from left at density=1 means x < 24 is dead
        val startX = 23f
        val result = isWithinEdgeDeadZone(startX, screenWidth, deadZoneDp = 24f, density = 1f)
        assertTrue(result)
    }

    @Test
    fun swipeDetector_rightEdgeIgnored_withinDeadZone() {
        val screenWidth = 360f
        // 24dp dead zone from right at density=1 means x > 336 is dead
        val startX = 340f
        val result = isWithinEdgeDeadZone(startX, screenWidth, deadZoneDp = 24f, density = 1f)
        assertTrue(result)
    }

    @Test
    fun swipeDetector_centerIsNotInDeadZone() {
        val screenWidth = 360f
        val startX = 180f
        val result = isWithinEdgeDeadZone(startX, screenWidth, deadZoneDp = 24f, density = 1f)
        assertFalse(result)
    }

    @Test
    fun swipeDetector_justOutsideLeftDeadZone_isNotDead() {
        val screenWidth = 360f
        // Exactly at 24dp is NOT in the dead zone (x < 24 is dead, x == 24 is not)
        val startX = 24f
        val result = isWithinEdgeDeadZone(startX, screenWidth, deadZoneDp = 24f, density = 1f)
        assertFalse(result)
    }

    @Test
    fun swipeDetector_highDensity_deadZoneScalesCorrectly() {
        val screenWidth = 720f  // 360dp at density=2
        val density = 2f
        // 24dp * 2 = 48px dead zone from each edge
        val startXInDeadZone = 40f
        val startXOutsideDeadZone = 50f
        assertTrue(isWithinEdgeDeadZone(startXInDeadZone, screenWidth, deadZoneDp = 24f, density = density))
        assertFalse(isWithinEdgeDeadZone(startXOutsideDeadZone, screenWidth, deadZoneDp = 24f, density = density))
    }
}
