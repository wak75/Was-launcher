package com.washim.washim_launcher.data

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.pm.SharedLibraryInfo
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.graphics.asImageBitmap
import com.washim.washim_launcher.data.model.AppInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

// ---------------------------------------------------------------------------
// Test doubles
// ---------------------------------------------------------------------------

/** Descriptor of a fake installed app used in unit tests. */
data class FakeApp(
    val packageName: String,
    val label: String,
    val hasLauncher: Boolean,
)

/**
 * Minimal [PackageManager] test double backed by a fixed list of [FakeApp]s.
 * [queryIntentActivities] returns only apps with [FakeApp.hasLauncher] = true.
 * All other methods return safe empty defaults or throw [UnsupportedOperationException].
 */
class FakePackageManager(private val apps: List<FakeApp>) : PackageManager() {

    @Suppress("DEPRECATION")
    override fun queryIntentActivities(intent: Intent, flags: Int): List<ResolveInfo> =
        getLauncherResolveInfos()

    override fun queryIntentActivities(
        intent: Intent,
        flags: ResolveInfoFlags,
    ): List<ResolveInfo> = getLauncherResolveInfos()

    /** Exposed for [TestableAppRepository] to bypass [Intent] construction. */
    fun getLauncherResolveInfos(): List<ResolveInfo> =
        apps.filter { it.hasLauncher }.map { app ->
            ResolveInfo().apply {
                activityInfo = ActivityInfo().apply {
                    packageName = app.packageName
                    name = app.label
                    nonLocalizedLabel = app.label
                    applicationInfo = android.content.pm.ApplicationInfo().apply {
                        packageName = app.packageName
                        nonLocalizedLabel = app.label
                    }
                }
                nonLocalizedLabel = app.label
            }
        }

    // ------------------------------------------------------------------
    // Remaining abstract method implementations — safe no-ops
    // ------------------------------------------------------------------
    override fun getPackageInfo(packageName: String, flags: Int) = throw UnsupportedOperationException()
    override fun getPackageInfo(packageName: String, flags: PackageInfoFlags) = throw UnsupportedOperationException()
    override fun getPackageInfo(versionedPackage: android.content.pm.VersionedPackage, flags: Int) = throw UnsupportedOperationException()
    override fun currentToCanonicalPackageNames(names: Array<out String>): Array<String> = names.toList().toTypedArray()
    override fun canonicalToCurrentPackageNames(names: Array<out String>): Array<String> = names.toList().toTypedArray()
    override fun getLaunchIntentForPackage(packageName: String): Intent? = null
    override fun getLeanbackLaunchIntentForPackage(packageName: String): Intent? = null
    override fun getPackageGids(packageName: String): IntArray = IntArray(0)
    override fun getPackageGids(packageName: String, flags: Int): IntArray = IntArray(0)
    override fun getPackageGids(packageName: String, flags: PackageInfoFlags): IntArray = IntArray(0)
    override fun getPackageUid(packageName: String, flags: Int): Int = 0
    override fun getPackageUid(packageName: String, flags: PackageInfoFlags): Int = 0
    override fun getPermissionInfo(name: String, flags: Int) = throw UnsupportedOperationException()
    override fun queryPermissionsByGroup(group: String?, flags: Int): List<android.content.pm.PermissionInfo> = emptyList()
    override fun getPermissionGroupInfo(name: String, flags: Int) = throw UnsupportedOperationException()
    override fun getAllPermissionGroups(flags: Int): List<android.content.pm.PermissionGroupInfo> = emptyList()
    override fun getApplicationInfo(packageName: String, flags: Int) = throw UnsupportedOperationException()
    override fun getApplicationInfo(packageName: String, flags: ApplicationInfoFlags) = throw UnsupportedOperationException()
    override fun getActivityInfo(component: android.content.ComponentName, flags: Int) = throw UnsupportedOperationException()
    override fun getActivityInfo(component: android.content.ComponentName, flags: ComponentInfoFlags) = throw UnsupportedOperationException()
    override fun getReceiverInfo(component: android.content.ComponentName, flags: Int) = throw UnsupportedOperationException()
    override fun getReceiverInfo(component: android.content.ComponentName, flags: ComponentInfoFlags) = throw UnsupportedOperationException()
    override fun getServiceInfo(component: android.content.ComponentName, flags: Int) = throw UnsupportedOperationException()
    override fun getServiceInfo(component: android.content.ComponentName, flags: ComponentInfoFlags) = throw UnsupportedOperationException()
    override fun getProviderInfo(component: android.content.ComponentName, flags: Int) = throw UnsupportedOperationException()
    override fun getProviderInfo(component: android.content.ComponentName, flags: ComponentInfoFlags) = throw UnsupportedOperationException()
    override fun getInstalledPackages(flags: Int): List<android.content.pm.PackageInfo> = emptyList()
    override fun getInstalledPackages(flags: PackageInfoFlags): List<android.content.pm.PackageInfo> = emptyList()
    override fun getPackagesHoldingPermissions(permissions: Array<out String>, flags: Int): List<android.content.pm.PackageInfo> = emptyList()
    override fun checkPermission(permName: String, pkgName: String): Int = PERMISSION_DENIED
    override fun isPermissionRevokedByPolicy(permName: String, pkgName: String): Boolean = false
    override fun addPermission(info: android.content.pm.PermissionInfo): Boolean = false
    override fun addPermissionAsync(info: android.content.pm.PermissionInfo): Boolean = false
    override fun removePermission(name: String) {}
    override fun checkSignatures(pkg1: String, pkg2: String): Int = SIGNATURE_UNKNOWN_PACKAGE
    override fun checkSignatures(uid1: Int, uid2: Int): Int = SIGNATURE_UNKNOWN_PACKAGE
    override fun getPackagesForUid(uid: Int): Array<String>? = null
    override fun getNameForUid(uid: Int): String? = null
    override fun getInstalledApplications(flags: Int): List<android.content.pm.ApplicationInfo> = emptyList()
    override fun getInstalledApplications(flags: ApplicationInfoFlags): List<android.content.pm.ApplicationInfo> = emptyList()
    override fun isInstantApp(): Boolean = false
    override fun isInstantApp(packageName: String): Boolean = false
    override fun getInstantAppCookieMaxBytes(): Int = 0
    override fun getInstantAppCookie(): ByteArray = ByteArray(0)
    override fun clearInstantAppCookie() {}
    override fun updateInstantAppCookie(cookie: ByteArray?) {}
    override fun getSystemSharedLibraryNames(): Array<String>? = null
    override fun getSharedLibraries(flags: Int): List<SharedLibraryInfo> = emptyList()
    override fun getChangedPackages(sequenceNumber: Int): android.content.pm.ChangedPackages? = null
    override fun getSystemAvailableFeatures(): Array<android.content.pm.FeatureInfo> = emptyArray()
    override fun hasSystemFeature(name: String): Boolean = false
    override fun hasSystemFeature(name: String, version: Int): Boolean = false
    override fun resolveActivity(intent: Intent, flags: Int): ResolveInfo? = null
    override fun resolveActivity(intent: Intent, flags: ResolveInfoFlags): ResolveInfo? = null
    override fun queryIntentActivityOptions(caller: android.content.ComponentName?, specifics: Array<out Intent>?, intent: Intent, flags: Int): List<ResolveInfo> = emptyList()
    override fun queryBroadcastReceivers(intent: Intent, flags: Int): List<ResolveInfo> = emptyList()
    override fun queryBroadcastReceivers(intent: Intent, flags: ResolveInfoFlags): List<ResolveInfo> = emptyList()
    override fun resolveService(intent: Intent, flags: Int): ResolveInfo? = null
    override fun resolveService(intent: Intent, flags: ResolveInfoFlags): ResolveInfo? = null
    override fun queryIntentServices(intent: Intent, flags: Int): List<ResolveInfo> = emptyList()
    override fun queryIntentServices(intent: Intent, flags: ResolveInfoFlags): List<ResolveInfo> = emptyList()
    override fun queryIntentContentProviders(intent: Intent, flags: Int): List<ResolveInfo> = emptyList()
    override fun queryIntentContentProviders(intent: Intent, flags: ResolveInfoFlags): List<ResolveInfo> = emptyList()
    override fun resolveContentProvider(name: String, flags: Int): android.content.pm.ProviderInfo? = null
    override fun resolveContentProvider(name: String, flags: ComponentInfoFlags): android.content.pm.ProviderInfo? = null
    override fun queryContentProviders(processName: String?, uid: Int, flags: Int): List<android.content.pm.ProviderInfo> = emptyList()
    override fun queryContentProviders(processName: String?, uid: Int, flags: ComponentInfoFlags): List<android.content.pm.ProviderInfo> = emptyList()
    override fun getInstrumentationInfo(className: android.content.ComponentName, flags: Int) = throw UnsupportedOperationException()
    override fun queryInstrumentation(targetPackage: String, flags: Int): List<android.content.pm.InstrumentationInfo> = emptyList()
    override fun getDrawable(packageName: String, resid: Int, appInfo: android.content.pm.ApplicationInfo?): android.graphics.drawable.Drawable? = null
    override fun getActivityIcon(activityName: android.content.ComponentName): android.graphics.drawable.Drawable = ColorDrawable()
    override fun getActivityIcon(intent: Intent): android.graphics.drawable.Drawable = ColorDrawable()
    override fun getActivityBanner(activityName: android.content.ComponentName): android.graphics.drawable.Drawable? = null
    override fun getActivityBanner(intent: Intent): android.graphics.drawable.Drawable? = null
    override fun getDefaultActivityIcon(): android.graphics.drawable.Drawable = ColorDrawable()
    override fun getApplicationIcon(info: android.content.pm.ApplicationInfo): android.graphics.drawable.Drawable = ColorDrawable()
    override fun getApplicationIcon(packageName: String): android.graphics.drawable.Drawable = ColorDrawable()
    override fun getApplicationBanner(info: android.content.pm.ApplicationInfo): android.graphics.drawable.Drawable? = null
    override fun getApplicationBanner(packageName: String): android.graphics.drawable.Drawable? = null
    override fun getActivityLogo(activityName: android.content.ComponentName): android.graphics.drawable.Drawable? = null
    override fun getActivityLogo(intent: Intent): android.graphics.drawable.Drawable? = null
    override fun getApplicationLogo(info: android.content.pm.ApplicationInfo): android.graphics.drawable.Drawable? = null
    override fun getApplicationLogo(packageName: String): android.graphics.drawable.Drawable? = null
    override fun getUserBadgedIcon(drawable: android.graphics.drawable.Drawable, user: android.os.UserHandle): android.graphics.drawable.Drawable = drawable
    override fun getUserBadgedDrawableForDensity(drawable: android.graphics.drawable.Drawable, user: android.os.UserHandle, badgeLocation: android.graphics.Rect?, badgeDensity: Int): android.graphics.drawable.Drawable = drawable
    override fun getUserBadgedLabel(label: CharSequence, user: android.os.UserHandle): CharSequence = label
    override fun getText(packageName: String, resid: Int, appInfo: android.content.pm.ApplicationInfo?): CharSequence? = null
    override fun getXml(packageName: String, resid: Int, appInfo: android.content.pm.ApplicationInfo?): android.content.res.XmlResourceParser? = null
    override fun getApplicationLabel(info: android.content.pm.ApplicationInfo): CharSequence = info.packageName ?: ""
    override fun getResourcesForActivity(activityName: android.content.ComponentName) = throw UnsupportedOperationException()
    override fun getResourcesForApplication(app: android.content.pm.ApplicationInfo) = throw UnsupportedOperationException()
    override fun getResourcesForApplication(packageName: String) = throw UnsupportedOperationException()
    override fun verifyPendingInstall(id: Int, verificationCode: Int) {}
    override fun extendVerificationTimeout(id: Int, verificationCodeAtTimeout: Int, millisecondsToDelay: Long) {}
    override fun setInstallerPackageName(targetPackage: String, installerPackageName: String?) {}
    override fun getInstallerPackageName(packageName: String): String? = null
    override fun getPackageInstaller(): android.content.pm.PackageInstaller = throw UnsupportedOperationException()
    override fun canRequestPackageInstalls(): Boolean = false
    override fun getPreferredPackages(flags: Int): List<android.content.pm.PackageInfo> = emptyList()
    override fun addPreferredActivity(filter: IntentFilter, match: Int, set: Array<out android.content.ComponentName>?, activity: android.content.ComponentName) {}
    override fun clearPackagePreferredActivities(packageName: String) {}
    override fun getPreferredActivities(outFilters: MutableList<IntentFilter>, outActivities: MutableList<android.content.ComponentName>, packageName: String?): Int = 0
    override fun setComponentEnabledSetting(componentName: android.content.ComponentName, newState: Int, flags: Int) {}
    override fun getComponentEnabledSetting(componentName: android.content.ComponentName): Int = COMPONENT_ENABLED_STATE_DEFAULT
    override fun setApplicationEnabledSetting(packageName: String, newState: Int, flags: Int) {}
    override fun getApplicationEnabledSetting(packageName: String): Int = COMPONENT_ENABLED_STATE_DEFAULT
    override fun setApplicationCategoryHint(packageName: String, categoryHint: Int) {}
    @Suppress("DEPRECATION")
    override fun addPackageToPreferred(packageName: String) {}
    @Suppress("DEPRECATION")
    override fun removePackageFromPreferred(packageName: String) {}
    override fun isSafeMode(): Boolean = false
}

// ---------------------------------------------------------------------------
// Test-specific AppRepository subclass — overrides launcher app querying
// ---------------------------------------------------------------------------

/**
 * [AppRepository] subclass for unit tests. Overrides [queryLauncherApps] to
 * return fake [ResolveInfo]s directly from [FakePackageManager], bypassing
 * [android.content.Intent] construction. Also overrides [resolveInfoToAppInfo]
 * to avoid loading real icons/labels.
 */
class TestableAppRepository(
    private val fakePackageManager: FakePackageManager,
    context: android.content.Context,
) : AppRepository(context, fakePackageManager) {

    override fun queryLauncherApps(): List<ResolveInfo> =
        fakePackageManager.getLauncherResolveInfos()

    override fun resolveInfoToAppInfo(resolveInfo: ResolveInfo): AppInfo {
        val activityInfo = resolveInfo.activityInfo
        val placeholder = android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
        return AppInfo(
            packageName = activityInfo.packageName,
            label = activityInfo.name ?: activityInfo.packageName,
            icon = placeholder.asImageBitmap(),
        )
    }

    /**
     * Overrides [getInstalledApps] to return a simple [flowOf] snapshot,
     * avoiding [ContextCompat.registerReceiver] which requires a real
     * application context with the dynamic-receiver permission granted.
     */
    override fun getInstalledApps(): Flow<List<AppInfo>> =
        flowOf(queryLauncherApps().map { resolveInfoToAppInfo(it) })
}

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [32])
class AppRepositoryTest {

    @Test
    fun getInstalledApps_excludesPackagesWithNoLauncherIntent() = runBlocking {
        val fakePackageManager = FakePackageManager(
            listOf(
                FakeApp("com.example.one", "One", hasLauncher = true),
                FakeApp("com.example.two", "Two", hasLauncher = false),
            )
        )
        val context = RuntimeEnvironment.getApplication()
        val repo = TestableAppRepository(fakePackageManager, context)
        val apps = repo.getInstalledApps().first()
        assertEquals(1, apps.size)
        assertEquals("com.example.one", apps[0].packageName)
    }
}
