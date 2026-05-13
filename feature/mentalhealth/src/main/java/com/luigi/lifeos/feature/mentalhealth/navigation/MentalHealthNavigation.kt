package com.luigi.lifeos.feature.mentalhealth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.mentalhealth.ui.MentalHealthScreen

/**
 * Navigation entry point for the mentalhealth feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val MentalHealthRoute = "mentalhealth"

fun NavGraphBuilder.mentalHealthScreen() {
    composable(route = MentalHealthRoute) {
        MentalHealthScreen()
    }
}
