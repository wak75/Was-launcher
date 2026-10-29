package com.washim.washim_launcher.ui.component

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.random.Random

private data class Star(
    val x: Float,         // fraction of width 0..1
    val y: Float,         // fraction of height 0..1
    val radius: Float,    // in dp
    val minAlpha: Float,
    val maxAlpha: Float,
    val periodMs: Int,
)

@Composable
fun StarField(
    modifier: Modifier = Modifier,
    starCount: Int = 60,
) {
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * (2.5f - 1f) + 1f, // 1..2.5 dp
                minAlpha = Random.nextFloat() * 0.2f,            // low twinkle base
                maxAlpha = Random.nextFloat() * 0.5f + 0.3f,    // brighter peak
                periodMs = Random.nextInt(2000, 6001),           // 2000..6000 ms
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "starField")

    // Animate alpha for each star — collect all State objects at composable scope
    val alphaStates: List<State<Float>> = stars.map { star ->
        infiniteTransition.animateFloat(
            initialValue = star.minAlpha,
            targetValue = star.maxAlpha,
            animationSpec = InfiniteRepeatableSpec(
                animation = tween(durationMillis = star.periodMs),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "starAlpha",
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        stars.forEachIndexed { index, star ->
            val alpha = alphaStates[index].value
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = star.radius.dp.toPx(),
                center = Offset(
                    x = star.x * size.width,
                    y = star.y * size.height,
                ),
            )
        }
    }
}
