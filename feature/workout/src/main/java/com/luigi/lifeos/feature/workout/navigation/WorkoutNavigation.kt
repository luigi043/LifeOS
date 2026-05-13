package com.luigi.lifeos.feature.workout.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.workout.ui.WorkoutScreen

/**
 * Navigation entry point for the workout feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val WorkoutRoute = "workout"

fun NavGraphBuilder.workoutScreen() {
    composable(route = WorkoutRoute) {
        WorkoutScreen()
    }
}
