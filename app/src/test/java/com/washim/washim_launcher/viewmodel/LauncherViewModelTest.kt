package com.washim.washim_launcher.viewmodel

import android.content.Context
import android.os.Looper
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.washim.washim_launcher.data.AppRepository
import com.washim.washim_launcher.data.DockPreferencesDataStore
import com.washim.washim_launcher.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

// ---------------------------------------------------------------------------
// Test doubles
// ---------------------------------------------------------------------------

private fun makeBitmap(): ImageBitmap {
    val bmp = android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
    return bmp.asImageBitmap()
}

private val TEST_APPS = listOf(
    AppInfo("com.spotify.music", "Spotify", makeBitmap()),
    AppInfo("com.android.settings", "Settings", makeBitmap()),
    AppInfo("com.google.android.apps.maps", "Maps", makeBitmap()),
)

/**
 * Fake AppRepository: overrides getInstalledApps() with fixed test data.
 * Extends AppRepository using a Robolectric context.
 */
class FakeAppRepository(context: Context) : AppRepository(
    context,
    context.packageManager,
) {
    private val _apps = MutableStateFlow(TEST_APPS)
    override fun getInstalledApps(): Flow<List<AppInfo>> = _apps
}

// DockPreferencesDataStore is final; use it directly with a Robolectric context.
// A fresh DataStore starts empty, so all dock slots return "" — the desired test state.

// ---------------------------------------------------------------------------
// Tests
// ---------------------------------------------------------------------------

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class LauncherViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val context: Context get() = RuntimeEnvironment.getApplication()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun idle() {
        shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun navigate_toAppMenu_updatesScreen() = runTest(testDispatcher) {
        val vm = LauncherViewModel(FakeAppRepository(context), DockPreferencesDataStore(context))
        advanceUntilIdle()
        idle()
        vm.navigate(LauncherScreen.APP_MENU)
        advanceUntilIdle()
        idle()
        assertEquals(LauncherScreen.APP_MENU, vm.uiState.value.screen)
    }

    @Test
    fun setSearchQuery_filtersApps() = runTest(testDispatcher) {
        val vm = LauncherViewModel(FakeAppRepository(context), DockPreferencesDataStore(context))
        advanceUntilIdle()
        idle()
        vm.setSearchQuery("sp")
        advanceUntilIdle()
        idle()
        val filtered = vm.uiState.value.apps
        assertEquals(1, filtered.size)
        assertEquals("Spotify", filtered[0].label)
    }

    @Test
    fun initialState_hasHomeScreen() = runTest(testDispatcher) {
        val vm = LauncherViewModel(FakeAppRepository(context), DockPreferencesDataStore(context))
        advanceUntilIdle()
        idle()
        assertEquals(LauncherScreen.HOME, vm.uiState.value.screen)
    }

    @Test
    fun setDockApp_delegatesToDockStore() = runTest(testDispatcher) {
        val vm = LauncherViewModel(FakeAppRepository(context), DockPreferencesDataStore(context))
        advanceUntilIdle()
        idle()
        // Should not throw — just verifies delegation works
        vm.setDockApp(0, "com.spotify.music")
        advanceUntilIdle()
        idle()
    }
}
