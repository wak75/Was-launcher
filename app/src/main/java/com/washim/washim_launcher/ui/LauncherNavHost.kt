package com.washim.washim_launcher.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.washim.washim_launcher.ui.screen.AppMenuScreen
import com.washim.washim_launcher.ui.screen.HomeScreen
import com.washim.washim_launcher.ui.screen.LockScreen
import com.washim.washim_launcher.ui.screen.RecentsScreen
import com.washim.washim_launcher.viewmodel.LauncherScreen
import com.washim.washim_launcher.viewmodel.LauncherViewModel
import com.washim.washim_launcher.viewmodel.LockViewModel

@Composable
fun LauncherNavHost(
    lockViewModel: LockViewModel,
    launcherViewModel: LauncherViewModel,
) {
    val navController = rememberNavController()

    val isLocked by lockViewModel.isLocked.collectAsStateWithLifecycle()
    val uiState by launcherViewModel.uiState.collectAsStateWithLifecycle()

    // When the device is unlocked, navigate from LOCK → HOME
    LaunchedEffect(isLocked) {
        if (!isLocked && navController.currentDestination?.route == LauncherScreen.LOCK.name) {
            navController.navigate(LauncherScreen.HOME.name) {
                popUpTo(LauncherScreen.LOCK.name) { inclusive = true }
            }
        }
    }

    // When the ViewModel screen state changes (swipe gestures set it), navigate accordingly
    LaunchedEffect(uiState.screen) {
        val target = uiState.screen.name
        if (navController.currentDestination?.route != target) {
            navController.navigate(target) {
                popUpTo(LauncherScreen.HOME.name) { inclusive = false }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = LauncherScreen.LOCK.name,
    ) {
        // LOCK screen — no BackHandler (system back should exit the launcher)
        composable(
            route = LauncherScreen.LOCK.name,
            exitTransition = {
                slideOutVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                ) { -it }
            },
        ) {
            LockScreen(
                viewModel = lockViewModel,
                onUnlocked = {
                    navController.navigate(LauncherScreen.HOME.name) {
                        popUpTo(LauncherScreen.LOCK.name) { inclusive = true }
                    }
                },
            )
        }

        // HOME screen — central hub; no BackHandler (let system back exit the launcher)
        composable(
            route = LauncherScreen.HOME.name,
            enterTransition = {
                when (initialState.destination.route) {
                    LauncherScreen.LOCK.name ->
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        ) { it }
                    LauncherScreen.APP_MENU.name ->
                        slideInHorizontally(
                            animationSpec = tween(durationMillis = 350),
                        ) { -it }
                    LauncherScreen.RECENTS.name ->
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        ) { -it }
                    else -> null
                }
            },
            exitTransition = {
                when (targetState.destination.route) {
                    LauncherScreen.APP_MENU.name ->
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = 350),
                        ) { -it }
                    LauncherScreen.RECENTS.name ->
                        slideOutVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                        ) { it }
                    else -> null
                }
            },
        ) {
            HomeScreen(viewModel = launcherViewModel)
        }

        // APP_MENU screen — slide in from right; back → HOME
        composable(
            route = LauncherScreen.APP_MENU.name,
            enterTransition = {
                slideInHorizontally(animationSpec = tween(durationMillis = 350)) { it }
            },
            exitTransition = {
                slideOutHorizontally(animationSpec = tween(durationMillis = 350)) { -it }
            },
        ) {
            BackHandler { launcherViewModel.navigate(LauncherScreen.HOME) }
            AppMenuScreen(viewModel = launcherViewModel)
        }

        // RECENTS screen — slide up from bottom; back → HOME
        composable(
            route = LauncherScreen.RECENTS.name,
            enterTransition = {
                slideInVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                ) { it }
            },
            exitTransition = {
                slideOutVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMediumLow,
                    ),
                ) { it }
            },
        ) {
            BackHandler { launcherViewModel.navigate(LauncherScreen.HOME) }
            RecentsScreen(viewModel = launcherViewModel)
        }
    }
}
