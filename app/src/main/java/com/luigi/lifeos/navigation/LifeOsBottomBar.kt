package com.luigi.lifeos.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.luigi.lifeos.feature.today.navigation.TodayRoute

/**
 * Bottom navigation. Single-source-of-truth selection is derived from the
 * NavController back stack so deep links / programmatic nav stay in sync.
 *
 * `saveState`/`restoreState` preserves each tab's scroll & UI state when
 * the user jumps between tabs — important for the Insights tab that holds
 * scroll-heavy chart screens.
 */
@Composable
fun LifeOsBottomBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar(modifier = modifier) {
        TopLevelDestination.entries.forEach { tab ->
            val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (tab.route == TodayRoute) {
                        // Restoring a saved detail stack here can reopen Finance instead of Today.
                        navController.popBackStack(TodayRoute, inclusive = false)
                    } else {
                    navController.navigate(tab.route) {
                        // Onboarding is removed after setup, so it cannot remain the pop target.
                        popUpTo(TodayRoute) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.label,
                    )
                },
                label = { Text(tab.label) },
            )
        }
    }
}
