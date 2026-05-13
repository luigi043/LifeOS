package com.luigi.lifeos.feature.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.settings.ui.SettingsScreen

/**
 * Navigation entry point for the settings feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val SettingsRoute = "settings"

fun NavGraphBuilder.settingsScreen() {
    composable(route = SettingsRoute) {
        SettingsScreen()
    }
}
