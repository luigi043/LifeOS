package com.luigi.lifeos.feature.habits.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.habits.ui.HabitsScreen

/**
 * Navigation entry point for the habits feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val HabitsRoute = "habits"

fun NavGraphBuilder.habitsScreen() {
    composable(route = HabitsRoute) {
        HabitsScreen()
    }
}
