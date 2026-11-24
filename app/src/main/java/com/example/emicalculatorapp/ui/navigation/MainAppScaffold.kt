package com.example.emicalculatorapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.emicalculatorapp.data.settings.SettingsRepository
import com.example.emicalculatorapp.ui.affordability.AffordabilityScreen
import com.example.emicalculatorapp.ui.calculator.CalculatorScreen
import com.example.emicalculatorapp.ui.compare.CompareScreen
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.saved.SavedQuotesScreen
import com.example.emicalculatorapp.ui.schedule.ScheduleScreen
import com.example.emicalculatorapp.ui.settings.SettingsBottomSheet
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.ElectricViolet
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.ui.theme.LuminousCyan
import com.example.emicalculatorapp.ui.theme.SpaceGroteskFamily
import com.example.emicalculatorapp.ui.theme.TextPrimary
import com.example.emicalculatorapp.ui.theme.TextSecondary
import com.example.emicalculatorapp.ui.tools.PrepaymentScreen
import com.example.emicalculatorapp.ui.tools.ToolsHubScreen
import com.example.emicalculatorapp.viewmodel.EMIViewModel

/**
 * Main application scaffold hosting the top bar, NavHost, and floating GlassBottomBar.
 * Holds the single shared EMIViewModel instance passed across all sub-screens.
 */
@Composable
fun MainAppScaffold(
    sharedViewModel: EMIViewModel,
    settingsRepository: SettingsRepository,
    onNavigateToGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Calculate.route
    val reduceMotion = LocalReduceMotion.current
    val density = LocalDensity.current
    val isKeyboardOpen = WindowInsets.ime.getBottom(density) > 0

    var showSettingsSheet by remember { mutableStateOf(false) }

    // Intercept Back button: if in sub-screen (Prepayment Simulator), return to Tools Hub.
    // Otherwise return to Calculate first before exiting.
    BackHandler(enabled = currentRoute != NavDestination.Calculate.route) {
        if (currentRoute == "prepayment_simulator") {
            navController.popBackStack()
        } else {
            navController.navigate(NavDestination.Calculate.route) {
                popUpTo(NavDestination.Calculate.route) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(CanvasColor)) {
        // Shared luminous ambient background
        AmbientBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo + Branding
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(LuminousCyan, ElectricViolet))
                                )
                        )
                        Text(
                            text = "EMIverse",
                            fontFamily = SpaceGroteskFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Action Icons: Help and Settings
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                navController.navigate(NavDestination.Tools.route) {
                                    popUpTo(NavDestination.Calculate.route)
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "Help & Tools",
                                tint = TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = { showSettingsSheet = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = !isKeyboardOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    GlassBottomBar(
                        currentRoute = currentRoute,
                        onNavigate = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(NavDestination.Calculate.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = if (isKeyboardOpen) 0.dp else innerPadding.calculateBottomPadding()
                    )
            ) {
                NavHost(
                    navController = navController,
                    startDestination = NavDestination.Calculate.route,
                    enterTransition = {
                        if (reduceMotion) fadeIn(tween(0))
                        else fadeIn(tween(250))
                    },
                    exitTransition = {
                        if (reduceMotion) fadeOut(tween(0))
                        else fadeOut(tween(200))
                    }
                ) {
                    composable(NavDestination.Calculate.route) {
                        CalculatorScreen(
                            onNavigateBack = onNavigateToGetStarted,
                            viewModel = sharedViewModel
                        )
                    }

                    composable(NavDestination.Schedule.route) {
                        ScheduleScreen(viewModel = sharedViewModel)
                    }

                    composable(NavDestination.Compare.route) {
                        CompareScreen(viewModel = sharedViewModel)
                    }

                    composable(NavDestination.Tools.route) {
                        ToolsHubScreen(
                            onNavigateToPrepayment = {
                                navController.navigate("prepayment_simulator")
                            },
                            onNavigateToAffordability = {
                                navController.navigate("affordability_calculator")
                            }
                        )
                    }

                    composable("prepayment_simulator") {
                        PrepaymentScreen(
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = sharedViewModel
                        )
                    }

                    composable("affordability_calculator") {
                        AffordabilityScreen(
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = sharedViewModel
                        )
                    }

                    composable(NavDestination.Saved.route) {
                        SavedQuotesScreen()
                    }
                }
            }
        }

        if (showSettingsSheet) {
            SettingsBottomSheet(
                settingsRepository = settingsRepository,
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}
