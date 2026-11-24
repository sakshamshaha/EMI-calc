package com.example.emicalculatorapp.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.emicalculatorapp.data.database.AppDatabase
import com.example.emicalculatorapp.data.database.QuoteRepository
import com.example.emicalculatorapp.data.database.ReportRepository
import com.example.emicalculatorapp.data.report.EmailReportSender
import com.example.emicalculatorapp.data.settings.SettingsRepository
import com.example.emicalculatorapp.ui.components.AmbientBackground
import com.example.emicalculatorapp.ui.settings.SettingsBottomSheet
import com.example.emicalculatorapp.ui.theme.CanvasColor
import com.example.emicalculatorapp.ui.theme.LocalReduceMotion
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import com.example.emicalculatorapp.viewmodel.ReportsState
import com.example.emicalculatorapp.viewmodel.SavedQuotesState

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

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedQuotesState = remember {
        val db = AppDatabase.getInstance(context.applicationContext)
        val repo = QuoteRepository(db.quoteDao())
        SavedQuotesState(coroutineScope, repo)
    }
    val reportsState = remember {
        val db = AppDatabase.getInstance(context.applicationContext)
        val repo = ReportRepository(db.reportDao())
        val sender = EmailReportSender()
        ReportsState(coroutineScope, repo, sender)
    }

    var showSettingsSheet by remember { mutableStateOf(false) }

    val isSubScreen = currentRoute in listOf(
        "prepayment_simulator",
        "affordability_calculator",
        "help_center",
        "report_problem"
    )

    // Intercept Back button: if in sub-screen, return to previous screen.
    // Otherwise return to Calculate first before exiting.
    BackHandler(enabled = currentRoute != NavDestination.Calculate.route) {
        if (isSubScreen) {
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
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                AppTopBar(
                    onHelpClick = {
                        navController.navigate("help_center") {
                            popUpTo(NavDestination.Calculate.route) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onSettingsClick = { showSettingsSheet = true }
                )
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
                AppNavGraph(
                    navController = navController,
                    sharedViewModel = sharedViewModel,
                    savedQuotesState = savedQuotesState,
                    reportsState = reportsState,
                    onNavigateToGetStarted = onNavigateToGetStarted,
                    reduceMotion = reduceMotion
                )
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
