package com.washim.washim_launcher.ui.screen

import android.content.Intent
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.washim.washim_launcher.ui.component.AnalogClock
import com.washim.washim_launcher.ui.component.StarField
import com.washim.washim_launcher.ui.gesture.detectLauncherSwipe
import com.washim.washim_launcher.ui.theme.Background
import com.washim.washim_launcher.ui.theme.OnSurface
import com.washim.washim_launcher.ui.theme.OnSurfaceMuted
import com.washim.washim_launcher.viewmodel.LockViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LockScreen(
    viewModel: LockViewModel,
    onUnlocked: () -> Unit,
) {
    val context = LocalContext.current

    val triggerBiometric: () -> Unit = remember(context) {
        {
            val activity = context as FragmentActivity
            val executor = ContextCompat.getMainExecutor(context)
            val prompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult,
                    ) {
                        viewModel.onAuthenticated()
                        onUnlocked()
                    }

                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence,
                    ) {
                        if (errorCode == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) {
                            val intent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                        BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                                )
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        }
                        // ERROR_NEGATIVE_BUTTON and other errors: no-op
                    }
                },
            )
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock Washim Launcher")
                .setNegativeButtonText("Cancel")
                .build()
            prompt.authenticate(promptInfo)
        }
    }

    val dateString = remember {
        SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
            .format(Date())
            .uppercase()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .pointerInput(Unit) {
                detectLauncherSwipe(
                    onLeft = {},
                    onRight = {},
                    onUp = { triggerBiometric() },
                    onDown = {},
                )
            },
    ) {
        StarField(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.semantics { contentDescription = "Analog clock" },
            ) {
                AnalogClock(modifier = Modifier.size(180.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = dateString,
                color = OnSurface,
                fontSize = 13.sp,
                letterSpacing = 0.15.em,
            )
        }

        UnlockHint(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
        )
    }
}

@Composable
private fun UnlockHint(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "unlockHint")
    val translationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "unlockHintY",
    )

    Column(
        modifier = modifier.graphicsLayer { this.translationY = translationY },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "SWIPE UP TO UNLOCK",
            color = OnSurfaceMuted,
            fontSize = 11.sp,
            letterSpacing = 0.12.em,
        )
    }
}
