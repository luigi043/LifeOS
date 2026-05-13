package com.luigi.lifeos.feature.finance.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.finance.ui.FinanceScreen

/**
 * Navigation entry point for the finance feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val FinanceRoute = "finance"

fun NavGraphBuilder.financeScreen() {
    composable(route = FinanceRoute) {
        FinanceScreen()
    }
}
