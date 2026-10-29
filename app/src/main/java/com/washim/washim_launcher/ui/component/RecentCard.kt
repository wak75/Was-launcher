package com.washim.washim_launcher.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.theme.OnSurface
import com.washim.washim_launcher.ui.theme.OnSurfaceMuted
import com.washim.washim_launcher.ui.theme.Surface

@Composable
fun RecentCard(
    app: AppInfo,
    thumbnail: ImageBitmap?,
    onDismiss: () -> Unit,
    onTap: () -> Unit,
) {
    var visible by remember { mutableStateOf(true) }

    // Compute a dominant color from the icon bitmap's average pixel as a fallback background.
    val fallbackColor = remember(app.icon) {
        computeDominantColor(app.icon)
    }

    AnimatedVisibility(
        visible = visible,
        exit = slideOutVertically { -it },
    ) {
        Box(
            modifier = Modifier
                .width(220.dp)
                .height(380.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .clickable { onTap() }
                .pointerInput(Unit) {
                    var cumulativeDrag = 0f
                    detectVerticalDragGestures(
                        onDragStart = { cumulativeDrag = 0f },
                        onVerticalDrag = { _, dragAmount ->
                            cumulativeDrag += dragAmount
                            if (cumulativeDrag < -50.dp.toPx()) {
                                visible = false
                                onDismiss()
                            }
                        },
                    )
                },
        ) {
            // Thumbnail / fallback area (fills card below the header)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 44.dp),
            ) {
                if (thumbnail != null) {
                    Image(
                        bitmap = thumbnail,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(fallbackColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            bitmap = app.icon,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                        )
                    }
                }
            }

            // Header bar (44.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Surface.copy(alpha = 0.95f))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    bitmap = app.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = app.label,
                    color = OnSurface,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        visible = false
                        onDismiss()
                    },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss ${app.label}",
                        tint = OnSurfaceMuted,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

/**
 * Computes a dominant color from the icon bitmap's average pixel value.
 * Samples a small area to keep it fast.
 */
private fun computeDominantColor(icon: ImageBitmap): Color {
    return try {
        val bmp = icon.asAndroidBitmap()
        val sampleSize = 4
        val stepX = (bmp.width / sampleSize).coerceAtLeast(1)
        val stepY = (bmp.height / sampleSize).coerceAtLeast(1)
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var count = 0
        var y = 0
        while (y < bmp.height) {
            var x = 0
            while (x < bmp.width) {
                val pixel = bmp.getPixel(x, y)
                rSum += (pixel shr 16) and 0xFF
                gSum += (pixel shr 8) and 0xFF
                bSum += pixel and 0xFF
                count++
                x += stepX
            }
            y += stepY
        }
        if (count == 0) {
            Color(0xFF1A1A2E)
        } else {
            val r = (rSum / count).toInt().coerceIn(0, 255)
            val g = (gSum / count).toInt().coerceIn(0, 255)
            val b = (bSum / count).toInt().coerceIn(0, 255)
            // Darken the average to keep the background dark
            Color(
                red = (r * 0.4f / 255f).coerceIn(0f, 1f),
                green = (g * 0.4f / 255f).coerceIn(0f, 1f),
                blue = (b * 0.4f / 255f).coerceIn(0f, 1f),
            )
        }
    } catch (_: Exception) {
        Color(0xFF1A1A2E)
    }
}
