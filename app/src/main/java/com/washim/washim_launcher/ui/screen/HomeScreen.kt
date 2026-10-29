package com.washim.washim_launcher.ui.screen

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.washim.washim_launcher.ui.component.AnalogClock
import com.washim.washim_launcher.ui.component.DockBar
import com.washim.washim_launcher.ui.component.DockEditSheet
import com.washim.washim_launcher.ui.component.StarField
import com.washim.washim_launcher.ui.gesture.detectLauncherSwipe
import com.washim.washim_launcher.ui.theme.OnSurface
import com.washim.washim_launcher.viewmodel.LauncherScreen
import com.washim.washim_launcher.viewmodel.LauncherViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(viewModel: LauncherViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    fun formatDate() = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date()).uppercase()
    val dateString by produceState(initialValue = formatDate()) {
        while (true) {
            val now = Calendar.getInstance()
            val msUntilMidnight = (24 * 60 * 60 * 1000L
                - now.get(Calendar.HOUR_OF_DAY) * 3600000L
                - now.get(Calendar.MINUTE) * 60000L
                - now.get(Calendar.SECOND) * 1000L)
            delay(msUntilMidnight)
            value = formatDate()
        }
    }

    fun launchApp(app: com.washim.washim_launcher.data.model.AppInfo) {
        context.packageManager.getLaunchIntentForPackage(app.packageName)
            ?.let { context.startActivity(it) }
    }

    var editingSlot by remember { mutableStateOf<Int?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectLauncherSwipe(
                    onLeft = { viewModel.navigate(LauncherScreen.APP_MENU) },
                    onRight = {},
                    onUp = { viewModel.navigate(LauncherScreen.RECENTS) },
                    onDown = {},
                )
            },
    ) {
        // Layer 1: Wallpaper
        uiState.wallpaper?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        // Layer 2: Dark scrim (85% opacity)
        Box(Modifier.fillMaxSize().background(Color(0xD9050508)))

        // Layer 3: StarField
        StarField(Modifier.fillMaxSize())

        // Layer 4: Centered content column
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.semantics { contentDescription = "Analog clock" },
            ) {
                AnalogClock(size = 180.dp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = dateString,
                color = OnSurface,
                fontSize = 13.sp,
            )
        }

        // Layer 5: Right-edge swipe hint (pulsing chevron)
        SwipeHintChevron(
            modifier = Modifier
                .align(CenterEnd)
                .padding(end = 12.dp),
        )

        // Layer 6: DockBar
        DockBar(
            apps = uiState.dockApps,
            onAppClick = { launchApp(it) },
            onAppLongClick = { slot, _ -> editingSlot = slot },
            modifier = Modifier
                .align(BottomCenter)
                .padding(bottom = 36.dp)
                .testTag("dock_bar"),
        )

        // Layer 7: Nav indicator bar
        Box(
            modifier = Modifier
                .align(BottomCenter)
                .padding(bottom = 8.dp)
                .size(100.dp, 4.dp)
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp)),
        )
    }

    editingSlot?.let { slot ->
        DockEditSheet(
            slot = slot,
            apps = uiState.apps,
            onSelect = { packageName ->
                viewModel.setDockApp(slot, packageName)
                editingSlot = null
            },
            onDismiss = { editingSlot = null },
        )
    }
}

@Composable
private fun SwipeHintChevron(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "swipeHint")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "swipeHintAlpha",
    )

    Text(
        text = "‹",
        color = Color.White.copy(alpha = alpha),
        fontSize = 32.sp,
        modifier = modifier,
    )
}
