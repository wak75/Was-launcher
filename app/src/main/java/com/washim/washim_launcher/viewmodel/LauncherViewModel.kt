package com.washim.washim_launcher.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.washim.washim_launcher.data.AppRepository
import com.washim.washim_launcher.data.DockPreferencesDataStore
import com.washim.washim_launcher.data.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class LauncherScreen { LOCK, HOME, APP_MENU, RECENTS }

data class LauncherUiState(
    val screen: LauncherScreen = LauncherScreen.HOME,
    val apps: List<AppInfo> = emptyList(),
    val dockApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val wallpaper: ImageBitmap? = null,
)

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val repo: AppRepository,
    private val dock: DockPreferencesDataStore,
) : ViewModel() {

    private val _screen = MutableStateFlow(LauncherScreen.HOME)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<LauncherUiState> = combine(
        repo.getInstalledApps(),
        dock.dockApps,
        _screen,
        _searchQuery,
    ) { installedApps, dockPackages, screen, query ->
        val filteredApps = installedApps.filter {
            it.label.contains(query, ignoreCase = true)
        }
        val resolvedDockApps = dockPackages.mapNotNull { pkg ->
            installedApps.find { it.packageName == pkg }
        }
        LauncherUiState(
            screen = screen,
            apps = filteredApps,
            dockApps = resolvedDockApps,
            searchQuery = query,
            wallpaper = repo.getWallpaper(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = LauncherUiState(),
    )

    fun navigate(screen: LauncherScreen) {
        _screen.value = screen
    }

    fun setSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setDockApp(slot: Int, packageName: String) {
        viewModelScope.launch {
            dock.setDockApp(slot, packageName)
        }
    }
}
