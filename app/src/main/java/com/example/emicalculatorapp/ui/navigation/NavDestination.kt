package com.example.emicalculatorapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Top-level destinations in EMIverse bottom navigation.
 */
sealed class NavDestination(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Calculate : NavDestination(
        route = "calculate",
        title = "Calculate",
        selectedIcon = Icons.Filled.Calculate,
        unselectedIcon = Icons.Outlined.Calculate
    )

    object Schedule : NavDestination(
        route = "schedule",
        title = "Schedule",
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    object Compare : NavDestination(
        route = "compare",
        title = "Compare",
        selectedIcon = Icons.AutoMirrored.Filled.CompareArrows,
        unselectedIcon = Icons.AutoMirrored.Outlined.CompareArrows
    )

    object Tools : NavDestination(
        route = "tools",
        title = "Tools",
        selectedIcon = Icons.Filled.Widgets,
        unselectedIcon = Icons.Outlined.Widgets
    )

    object Saved : NavDestination(
        route = "saved",
        title = "Saved",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder
    )

    companion object {
        val items: List<NavDestination>
            get() = listOf(Calculate, Schedule, Compare, Tools, Saved)
    }
}
