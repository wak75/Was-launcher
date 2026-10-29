package com.washim.washim_launcher.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.washim.washim_launcher.ui.theme.ClockHour
import com.washim.washim_launcher.ui.theme.ClockMinute
import com.washim.washim_launcher.ui.theme.ClockSecond
import java.util.Calendar

@Composable
fun AnalogClock(
    modifier: Modifier = Modifier,
    size: Dp = 180.dp,
) {
    var time by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { frameTimeMs ->
                time = frameTimeMs
            }
        }
    }

    Canvas(modifier = modifier.size(size)) {
        val cal = Calendar.getInstance().apply { timeInMillis = time }
        val hour = cal.get(Calendar.HOUR)       // 0-11
        val minute = cal.get(Calendar.MINUTE)   // 0-59
        val second = cal.get(Calendar.SECOND)   // 0-59

        val hourDeg = hour * 30f + minute * 0.5f
        val minuteDeg = minute * 6f + second * 0.1f
        val secondDeg = second * 6f

        val centerX = this.size.width / 2f
        val centerY = this.size.height / 2f
        val center = Offset(centerX, centerY)

        val outerRadius = size.toPx() / 2f

        // Outer ring
        drawCircle(
            color = Color.White.copy(alpha = 0.06f),
            radius = outerRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx()),
        )

        // 4 hour ticks at 12/3/6/9 positions
        for (i in 0 until 4) {
            val tickDeg = i * 90f
            rotate(degrees = tickDeg, pivot = center) {
                val startRadius = 80.dp.toPx()
                val endRadius = 76.dp.toPx()
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(centerX, centerY - startRadius),
                    end = Offset(centerX, centerY - endRadius),
                    strokeWidth = 1.5.dp.toPx(),
                )
            }
        }

        // 56 minute ticks (all 60 positions minus the 4 hour positions)
        for (i in 0 until 60) {
            if (i % 15 == 0) continue // skip the 4 hour tick positions
            val tickDeg = i * 6f
            rotate(degrees = tickDeg, pivot = center) {
                val startRadius = 80.dp.toPx()
                val endRadius = 78.dp.toPx()
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(centerX, centerY - startRadius),
                    end = Offset(centerX, centerY - endRadius),
                    strokeWidth = 1.dp.toPx(),
                )
            }
        }

        // Hour hand
        rotate(degrees = hourDeg, pivot = center) {
            drawLine(
                color = ClockHour,
                start = center,
                end = Offset(centerX, centerY - 45.dp.toPx()),
                strokeWidth = 2.5.dp.toPx(),
            )
        }

        // Minute hand
        rotate(degrees = minuteDeg, pivot = center) {
            drawLine(
                color = ClockMinute,
                start = center,
                end = Offset(centerX, centerY - 30.dp.toPx()),
                strokeWidth = 1.5.dp.toPx(),
            )
        }

        // Second hand (tail 5dp past center, tip 22dp from center)
        rotate(degrees = secondDeg, pivot = center) {
            drawLine(
                color = ClockSecond,
                start = Offset(centerX, centerY + 5.dp.toPx()),
                end = Offset(centerX, centerY - 22.dp.toPx()),
                strokeWidth = 0.8.dp.toPx(),
            )
        }

        // Center dot: filled circle 3dp
        drawCircle(
            color = ClockSecond,
            radius = 3.dp.toPx(),
            center = center,
        )

        // Center dot overlay: 1.2dp black
        drawCircle(
            color = Color.Black,
            radius = 1.2.dp.toPx(),
            center = center,
        )
    }
}
