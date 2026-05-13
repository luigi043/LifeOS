package com.luigi.lifeos.feature.grocery.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.grocery.ui.GroceryScreen

/**
 * Navigation entry point for the grocery feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val GroceryRoute = "grocery"

fun NavGraphBuilder.groceryScreen() {
    composable(route = GroceryRoute) {
        GroceryScreen()
    }
}
