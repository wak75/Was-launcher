package com.washim.washim_launcher.ui.gesture

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import kotlin.math.abs

/**
 * Returns true when [x] falls within the edge dead-zone on either the left or right side.
 * This is a pure function exposed for unit testing.
 *
 * @param x         The pointer's x-position in pixels.
 * @param screenWidth  The width of the screen in pixels.
 * @param deadZoneDp   The dead-zone width in dp.
 * @param density      Screen density (dp-to-px ratio).
 */
fun isWithinEdgeDeadZone(
    x: Float,
    screenWidth: Float,
    deadZoneDp: Float,
    density: Float,
): Boolean {
    val deadZonePx = deadZoneDp * density
    return x < deadZonePx || x > screenWidth - deadZonePx
}

/**
 * Detects directional swipes within a [PointerInputScope] and dispatches exactly one callback
 * per gesture.
 *
 * Edge dead-zone: horizontal swipes that start within 24dp of either the left or right edge are
 * ignored (system back-gesture zone).
 *
 * Minimum travel: 30dp in the dominant axis.
 * Axis dominance: horizontal if abs(dx) > abs(dy) * 1.5; vertical if abs(dy) > abs(dx) * 1.2.
 */
suspend fun PointerInputScope.detectLauncherSwipe(
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit,
) {
    val edgeDeadZoneDp = 24f
    val minTravelDp = 30f
    val minTravelPx = minTravelDp * density

    while (true) {
        var startPosition: Offset = Offset.Zero

        // Wait for the first finger down.
        awaitPointerEventScope {
            val down = awaitFirstDown(requireUnconsumed = false)
            startPosition = down.position
        }

        val startX = startPosition.x
        val startY = startPosition.y
        val screenWidth = size.width.toFloat()

        val startedInHorizontalDeadZone =
            isWithinEdgeDeadZone(startX, screenWidth, edgeDeadZoneDp, density)

        // Now track the pointer until it is released.
        awaitPointerEventScope {
            var endPosition = startPosition

            while (true) {
                val event = awaitPointerEvent()
                val pointer = event.changes.firstOrNull() ?: break

                if (pointer.pressed) {
                    endPosition = pointer.position
                } else {
                    // Pointer released — compute direction.
                    endPosition = pointer.position
                    val dx = endPosition.x - startX
                    val dy = endPosition.y - startY
                    val absDx = abs(dx)
                    val absDy = abs(dy)

                    val isHorizontalDominant = absDx > absDy * 1.5f
                    val isVerticalDominant = absDy > absDx * 1.2f

                    when {
                        isHorizontalDominant && absDx >= minTravelPx && !startedInHorizontalDeadZone -> {
                            if (dx < 0) onLeft() else onRight()
                        }
                        isVerticalDominant && absDy >= minTravelPx -> {
                            if (dy < 0) onUp() else onDown()
                        }
                    }
                    break
                }
            }
        }
    }
}
