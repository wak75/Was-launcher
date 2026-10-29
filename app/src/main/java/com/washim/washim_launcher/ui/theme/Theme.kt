package com.washim.washim_launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LauncherColorScheme = darkColorScheme(
    background = Background,
    surface = Surface,
    onSurface = OnSurface,
    primary = Accent,
    onBackground = OnSurface,
    surfaceVariant = DockSurface,
    outline = DockBorder,
)

@Composable
fun LauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LauncherColorScheme,
        content = content,
    )
}
