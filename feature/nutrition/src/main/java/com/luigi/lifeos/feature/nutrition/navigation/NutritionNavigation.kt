package com.luigi.lifeos.feature.nutrition.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.nutrition.ui.NutritionScreen

/**
 * Navigation entry point for the nutrition feature.
 * Route string is module-public so :app (or other features doing
 * deep-link nav) can target it without referencing internals.
 */
const val NutritionRoute = "nutrition"

fun NavGraphBuilder.nutritionScreen() {
    composable(route = NutritionRoute) {
        NutritionScreen()
    }
}
