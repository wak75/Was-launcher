package com.washim.washim_launcher.data

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.PackageManager.MATCH_ALL
import android.content.pm.ResolveInfo
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.washim.washim_launcher.data.model.AppInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val packageManager: PackageManager,
) {
    /**
     * Returns the list of launcher [ResolveInfo] entries from PackageManager.
     * Declared `internal open` so unit-test subclasses can bypass
     * [android.content.Intent] construction (which is not available in
     * pure JVM unit tests without Robolectric).
     */
    internal open fun queryLauncherApps(): List<ResolveInfo> {
        val intent = Intent(ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(MATCH_ALL.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, MATCH_ALL)
        }
    }

    /**
     * Converts a [ResolveInfo] to [AppInfo]. Declared `internal open` so
     * unit-test subclasses can return a stub [AppInfo] without invoking
     * Android Bitmap / Resources APIs.
     */
    internal open fun resolveInfoToAppInfo(resolveInfo: ResolveInfo): AppInfo {
        val activityInfo = resolveInfo.activityInfo
        return AppInfo(
            packageName = activityInfo.packageName,
            label = resolveInfo.loadLabel(packageManager).toString(),
            icon = resolveInfo.loadIcon(packageManager).toBitmap().asImageBitmap(),
        )
    }

    open fun getInstalledApps(): Flow<List<AppInfo>> = callbackFlow {
        fun buildAppList(): List<AppInfo> =
            queryLauncherApps().map { resolveInfoToAppInfo(it) }

        trySend(buildAppList())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(buildAppList())
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    open fun getWallpaper(): ImageBitmap? =
        WallpaperManager.getInstance(context).drawable?.toBitmap()?.asImageBitmap()
}
