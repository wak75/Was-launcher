package com.washim.washim_launcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dock_prefs")

@Singleton
class DockPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val DOCK_SLOT_0 = stringPreferencesKey("dock_slot_0")
    private val DOCK_SLOT_1 = stringPreferencesKey("dock_slot_1")
    private val DOCK_SLOT_2 = stringPreferencesKey("dock_slot_2")
    private val DOCK_SLOT_3 = stringPreferencesKey("dock_slot_3")

    private val slotKeys = listOf(DOCK_SLOT_0, DOCK_SLOT_1, DOCK_SLOT_2, DOCK_SLOT_3)

    val dockApps: Flow<List<String>> = context.dataStore.data.map { preferences ->
        slotKeys.map { key -> preferences[key] ?: "" }
    }

    suspend fun setDockApp(slot: Int, packageName: String) {
        require(slot in 0..3) { "Dock slot must be between 0 and 3, got $slot" }
        context.dataStore.edit { preferences ->
            preferences[slotKeys[slot]] = packageName
        }
    }
}
