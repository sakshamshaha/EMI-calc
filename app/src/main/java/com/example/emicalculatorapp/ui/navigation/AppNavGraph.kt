package com.example.emicalculatorapp.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.emicalculatorapp.data.settings.EffectsLevel
import com.example.emicalculatorapp.model.QuoteCalculations
import com.example.emicalculatorapp.ui.affordability.AffordabilityScreen
import com.example.emicalculatorapp.ui.calculator.CalculatorScreen
import com.example.emicalculatorapp.ui.compare.CompareScreen
import com.example.emicalculatorapp.ui.help.HelpCenterScreen
import com.example.emicalculatorapp.ui.report.ReportProblemScreen
import com.example.emicalculatorapp.ui.saved.SavedQuotesScreen
import com.example.emicalculatorapp.ui.schedule.ScheduleScreen
import com.example.emicalculatorapp.ui.theme.LocalEffectsLevel
import com.example.emicalculatorapp.ui.tools.PrepaymentScreen
import com.example.emicalculatorapp.ui.tools.ToolsHubScreen
import com.example.emicalculatorapp.viewmodel.EMIViewModel
import com.example.emicalculatorapp.viewmodel.ReportsState
import com.example.emicalculatorapp.viewmodel.SavedQuotesState

private fun getNavTabIndex(route: String?): Int? = when (route) {
    NavDestination.Calculate.route -> 0
    NavDestination.Schedule.route -> 1
    NavDestination.Compare.route -> 2
    NavDestination.Tools.route -> 3
    NavDestination.Saved.route -> 4
    else -> null
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sharedViewModel: EMIViewModel,
    savedQuotesState: SavedQuotesState,
    reportsState: ReportsState,
    onNavigateToGetStarted: () -> Unit,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val effectsLevel = LocalEffectsLevel.current
    val isLiteOrReduced = reduceMotion || effectsLevel == EffectsLevel.LITE

    NavHost(
        navController = navController,
        startDestination = NavDestination.Calculate.route,
        enterTransition = {
            if (isLiteOrReduced) {
                fadeIn(tween(150))
            } else {
                val fromIdx = getNavTabIndex(initialState.destination.route)
                val toIdx = getNavTabIndex(targetState.destination.route)
                if (fromIdx != null && toIdx != null) {
                    if (toIdx > fromIdx) {
                        slideInHorizontally(tween(250)) { it / 3 } + fadeIn(tween(250))
                    } else {
                        slideInHorizontally(tween(250)) { -it / 3 } + fadeIn(tween(250))
                    }
                } else {
                    slideInHorizontally(tween(250)) { it } + fadeIn(tween(250))
                }
            }
        },
        exitTransition = {
            if (isLiteOrReduced) {
                fadeOut(tween(150))
            } else {
                val fromIdx = getNavTabIndex(initialState.destination.route)
                val toIdx = getNavTabIndex(targetState.destination.route)
                if (fromIdx != null && toIdx != null) {
                    if (toIdx > fromIdx) {
                        slideOutHorizontally(tween(250)) { -it / 3 } + fadeOut(tween(200))
                    } else {
                        slideOutHorizontally(tween(250)) { it / 3 } + fadeOut(tween(200))
                    }
                } else {
                    slideOutHorizontally(tween(250)) { -it / 4 } + fadeOut(tween(200))
                }
            }
        },
        popEnterTransition = {
            if (isLiteOrReduced) {
                fadeIn(tween(150))
            } else {
                val fromIdx = getNavTabIndex(initialState.destination.route)
                val toIdx = getNavTabIndex(targetState.destination.route)
                if (fromIdx != null && toIdx != null) {
                    if (toIdx > fromIdx) {
                        slideInHorizontally(tween(250)) { it / 3 } + fadeIn(tween(250))
                    } else {
                        slideInHorizontally(tween(250)) { -it / 3 } + fadeIn(tween(250))
                    }
                } else {
                    slideInHorizontally(tween(250)) { -it / 4 } + fadeIn(tween(250))
                }
            }
        },
        popExitTransition = {
            if (isLiteOrReduced) {
                fadeOut(tween(150))
            } else {
                val fromIdx = getNavTabIndex(initialState.destination.route)
                val toIdx = getNavTabIndex(targetState.destination.route)
                if (fromIdx != null && toIdx != null) {
                    if (toIdx > fromIdx) {
                        slideOutHorizontally(tween(250)) { -it / 3 } + fadeOut(tween(200))
                    } else {
                        slideOutHorizontally(tween(250)) { it / 3 } + fadeOut(tween(200))
                    }
                } else {
                    slideOutHorizontally(tween(250)) { it } + fadeOut(tween(200))
                }
            }
        },
        modifier = modifier
    ) {
        composable(NavDestination.Calculate.route) {
            CalculatorScreen(
                onNavigateBack = onNavigateToGetStarted,
                viewModel = sharedViewModel,
                savedQuotesState = savedQuotesState,
                onNavigateToSaved = {
                    navController.navigate(NavDestination.Saved.route) {
                        popUpTo(NavDestination.Calculate.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(NavDestination.Schedule.route) {
            ScheduleScreen(viewModel = sharedViewModel)
        }

        composable(NavDestination.Compare.route) {
            CompareScreen(
                viewModel = sharedViewModel,
                savedQuotesState = savedQuotesState,
                onNavigateToSaved = {
                    navController.navigate(NavDestination.Saved.route) {
                        popUpTo(NavDestination.Calculate.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(NavDestination.Tools.route) {
            ToolsHubScreen(
                onNavigateToPrepayment = {
                    navController.navigate("prepayment_simulator")
                },
                onNavigateToAffordability = {
                    navController.navigate("affordability_calculator")
                },
                onNavigateToHelpCenter = {
                    navController.navigate("help_center")
                },
                onNavigateToReportProblem = {
                    navController.navigate("report_problem")
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

        composable("help_center") {
            HelpCenterScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReport = { navController.navigate("report_problem") }
            )
        }

        composable("report_problem") {
            ReportProblemScreen(
                onNavigateBack = { navController.popBackStack() },
                reportsState = reportsState,
                viewModel = sharedViewModel
            )
        }

        composable(NavDestination.Saved.route) {
            SavedQuotesScreen(
                savedQuotesState = savedQuotesState,
                onLoadQuoteIntoCalculate = { quote ->
                    val mapped = QuoteCalculations.mapQuoteToLoanState(quote)
                    sharedViewModel.loadQuote(
                        category = mapped.category,
                        amount = mapped.amount,
                        rate = mapped.rate,
                        tenureValue = mapped.tenureValue,
                        isYearMode = mapped.isYearMode
                    )
                    navController.navigate(NavDestination.Calculate.route) {
                        popUpTo(NavDestination.Calculate.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onCompareTwoQuotes = { quoteA, quoteB ->
                    val mappedA = QuoteCalculations.mapQuoteToLoanState(quoteA)
                    val mappedB = QuoteCalculations.mapQuoteToLoanState(quoteB)
                    sharedViewModel.loadQuote(
                        category = mappedA.category,
                        amount = mappedA.amount,
                        rate = mappedA.rate,
                        tenureValue = mappedA.tenureValue,
                        isYearMode = mappedA.isYearMode
                    )
                    sharedViewModel.setLoanB(
                        amount = mappedB.amount,
                        rate = mappedB.rate,
                        tenureValue = mappedB.tenureValue,
                        isYearMode = mappedB.isYearMode,
                        processingFeePercent = mappedB.processingFeePercent
                    )
                    navController.navigate(NavDestination.Compare.route) {
                        popUpTo(NavDestination.Calculate.route) { saveState = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToCalculate = {
                    navController.navigate(NavDestination.Calculate.route) {
                        popUpTo(NavDestination.Calculate.route) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
