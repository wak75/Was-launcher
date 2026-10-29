package com.washim.washim_launcher.ui.screen

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.component.RecentCard
import com.washim.washim_launcher.ui.gesture.detectLauncherSwipe
import com.washim.washim_launcher.ui.theme.Background
import com.washim.washim_launcher.ui.theme.OnSurfaceMuted
import com.washim.washim_launcher.viewmodel.LauncherScreen
import com.washim.washim_launcher.viewmodel.LauncherViewModel
import kotlinx.coroutines.flow.first

@Composable
fun RecentsScreen(viewModel: LauncherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Load recent apps list once when entering the screen.
    val recentApps by produceState<List<AppInfo>>(initialValue = emptyList()) {
        val allApps = viewModel.uiState.first { it.apps.isNotEmpty() }.apps
        value = buildRecentApps(context, allApps)
    }

    // Track dismissed package names locally.
    val dismissed = remember { mutableStateListOf<String>() }

    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .pointerInput(Unit) {
                detectLauncherSwipe(
                    onLeft = {},
                    onRight = {},
                    onUp = {},
                    onDown = { viewModel.navigate(LauncherScreen.HOME) },
                )
            },
    ) {
        // Screen label
        Text(
            text = "RECENT",
            color = OnSurfaceMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.2.em,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp, top = 52.dp),
        )

        // Card carousel
        val visibleApps = recentApps.filter { it.packageName !in dismissed }
        LazyRow(
            state = lazyListState,
            flingBehavior = snapFlingBehavior,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 32.dp),
            modifier = Modifier.align(Alignment.Center),
        ) {
            items(visibleApps, key = { it.packageName }) { app ->
                RecentCard(
                    app = app,
                    thumbnail = null,
                    onDismiss = { dismissed.add(app.packageName) },
                    onTap = {
                        launchApp(context, app)
                        viewModel.navigate(LauncherScreen.HOME)
                    },
                )
            }
        }

        // "Clear all" button
        TextButton(
            onClick = {
                dismissed.addAll(recentApps.map { it.packageName })
                viewModel.navigate(LauncherScreen.HOME)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
        ) {
            Text(
                text = "CLEAR ALL",
                color = OnSurfaceMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.15.em,
            )
        }
    }
}

/**
 * Builds the recent apps list.
 * If PACKAGE_USAGE_STATS permission is granted, queries last 1 hour of usage stats,
 * sorts by lastTimeUsed descending, and maps to AppInfo.
 * Falls back to the first 6 installed apps if permission is not granted.
 */
private fun buildRecentApps(context: Context, allApps: List<AppInfo>): List<AppInfo> {
    return if (hasUsageStatsPermission(context)) {
        getRecentAppsFromUsageStats(context, allApps)
    } else {
        allApps.take(6)
    }
}

private fun hasUsageStatsPermission(context: Context): Boolean {
    return try {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val uid = android.os.Process.myUid()
        val packageName = context.packageName
        @Suppress("DEPRECATION")
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            uid,
            packageName,
        )
        mode == AppOpsManager.MODE_ALLOWED
    } catch (_: Exception) {
        false
    }
}

private fun getRecentAppsFromUsageStats(context: Context, allApps: List<AppInfo>): List<AppInfo> {
    return try {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 60 * 60 * 1000L // last 1 hour

        val usageStats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_BEST,
            startTime,
            endTime,
        ) ?: return allApps.take(6)

        val appMap = allApps.associateBy { it.packageName }

        usageStats
            .filter { stat -> appMap.containsKey(stat.packageName) && stat.lastTimeUsed > 0 }
            .sortedByDescending { it.lastTimeUsed }
            .take(10)
            .mapNotNull { stat -> appMap[stat.packageName] }
            .ifEmpty { allApps.take(6) }
    } catch (_: Exception) {
        allApps.take(6)
    }
}

private fun launchApp(context: Context, app: AppInfo) {
    context.packageManager.getLaunchIntentForPackage(app.packageName)?.let {
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(it)
    }
}
