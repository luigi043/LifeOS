package com.luigi.lifeos.feature.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.profile.ui.ProfileScreen

/**
 * Navigation entry point for the profile feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val ProfileRoute = "profile"

fun NavGraphBuilder.profileScreen() {
    composable(route = ProfileRoute) {
        ProfileScreen()
    }
}
