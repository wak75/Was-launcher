package com.washim.washim_launcher.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import com.washim.washim_launcher.data.model.AppInfo
import kotlinx.coroutines.isActive
import kotlin.math.abs

@Composable
fun ZoomScaleList(
    items: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scales = remember { mutableStateListOf<Float>().apply { repeat(items.size) { add(1f) } } }

    // Resize scales list if items size changes
    if (scales.size != items.size) {
        scales.clear()
        repeat(items.size) { scales.add(1f) }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            withFrameMillis {
                val layoutInfo = listState.layoutInfo
                val viewportHeight = layoutInfo.viewportSize.height
                if (viewportHeight == 0) return@withFrameMillis

                val viewportCenterY = layoutInfo.viewportStartOffset + viewportHeight / 2f

                layoutInfo.visibleItemsInfo.forEach { item ->
                    val index = item.index
                    if (index < scales.size) {
                        val itemCenterY = item.offset + item.size / 2f
                        val distNorm = (abs(itemCenterY - viewportCenterY) / (viewportHeight / 2f))
                            .coerceIn(0f, 1f)
                        scales[index] = 1f - distNorm * 0.18f
                    }
                }
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
    ) {
        itemsIndexed(items) { index, app ->
            val scale = if (index < scales.size) scales[index] else 1f
            val layoutInfo = listState.layoutInfo
            val viewportHeight = layoutInfo.viewportSize.height
            val alpha = if (viewportHeight == 0) {
                1f
            } else {
                val viewportCenterY = layoutInfo.viewportStartOffset + viewportHeight / 2f
                val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == index }
                if (visibleItem != null) {
                    val itemCenterY = visibleItem.offset + visibleItem.size / 2f
                    val distNorm = (abs(itemCenterY - viewportCenterY) / (viewportHeight / 2f))
                        .coerceIn(0f, 1f)
                    1f - distNorm * 0.65f
                } else {
                    1f
                }
            }
            AppListItem(
                app = app,
                scale = scale,
                alpha = alpha,
                onClick = { onAppClick(app) },
            )
        }
    }
}
