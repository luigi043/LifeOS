package com.luigi.lifeos.feature.analytics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.analytics.ui.AnalyticsScreen

/**
 * Navigation entry point for the analytics feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val AnalyticsRoute = "analytics"

fun NavGraphBuilder.analyticsScreen() {
    composable(route = AnalyticsRoute) {
        AnalyticsScreen()
    }
}
