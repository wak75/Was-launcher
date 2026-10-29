package com.washim.washim_launcher.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.washim.washim_launcher.data.model.AppInfo
import com.washim.washim_launcher.ui.component.ZoomScaleList
import com.washim.washim_launcher.ui.gesture.detectLauncherSwipe
import com.washim.washim_launcher.ui.theme.Accent
import com.washim.washim_launcher.ui.theme.OnSurface
import com.washim.washim_launcher.ui.theme.OnSurfaceMuted
import com.washim.washim_launcher.viewmodel.LauncherScreen
import com.washim.washim_launcher.viewmodel.LauncherViewModel

@Composable
fun AppMenuScreen(viewModel: LauncherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF7040408))
            .pointerInput(Unit) {
                detectLauncherSwipe(
                    onLeft = {},
                    onRight = { viewModel.navigate(LauncherScreen.HOME) },
                    onUp = {},
                    onDown = {},
                )
            },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 48.dp),
        ) {
            Text(
                text = "APPS",
                color = OnSurfaceMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.em,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                placeholder = {
                    Text(
                        text = "Search apps…",
                        color = OnSurfaceMuted,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = OnSurfaceMuted,
                    )
                },
                trailingIcon = if (uiState.searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                tint = OnSurfaceMuted,
                            )
                        }
                    }
                } else null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = OnSurfaceMuted.copy(alpha = 0.3f),
                    cursorColor = Accent,
                    focusedTextColor = OnSurface,
                    unfocusedTextColor = OnSurface,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            )

            Spacer(modifier = Modifier.height(16.dp))

            ZoomScaleList(
                items = uiState.apps,
                onAppClick = { app ->
                    launchApp(context, app)
                    viewModel.navigate(LauncherScreen.HOME)
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun launchApp(context: Context, app: AppInfo) {
    context.packageManager.getLaunchIntentForPackage(app.packageName)
        ?.let { context.startActivity(it) }
}
