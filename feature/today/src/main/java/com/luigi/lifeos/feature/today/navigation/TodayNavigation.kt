package com.luigi.lifeos.feature.today.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.today.ui.TodayScreen

/**
 * Navigation entry point for the today feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val TodayRoute = "today"

fun NavGraphBuilder.todayScreen(onNavigate: (String) -> Unit) {
    composable(route = TodayRoute) {
        TodayScreen(onNavigate = onNavigate)
    }
}
