package com.luigi.lifeos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.luigi.lifeos.feature.analytics.navigation.analyticsScreen
import com.luigi.lifeos.feature.finance.navigation.financeScreen
import com.luigi.lifeos.feature.grocery.navigation.groceryScreen
import com.luigi.lifeos.feature.habits.navigation.habitsScreen
import com.luigi.lifeos.feature.mentalhealth.navigation.mentalHealthScreen
import com.luigi.lifeos.feature.nutrition.navigation.nutritionScreen
import com.luigi.lifeos.feature.profile.navigation.profileScreen
import com.luigi.lifeos.feature.settings.navigation.settingsScreen
import com.luigi.lifeos.feature.today.navigation.TodayRoute
import com.luigi.lifeos.feature.today.navigation.todayScreen
import com.luigi.lifeos.feature.workout.navigation.workoutScreen

/**
 * Single source of truth for navigation across the app.
 *
 * Each feature module exposes a `*Screen()` `NavGraphBuilder` extension
 * (the standard Now-in-Android pattern). This file *only* composes them —
 * it has zero feature-internal knowledge, which keeps :app decoupled.
 */
@Composable
fun LifeOsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = TodayRoute,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        todayScreen()
        financeScreen()
        workoutScreen()
        nutritionScreen()
        groceryScreen()
        mentalHealthScreen()
        habitsScreen()
        analyticsScreen()
        profileScreen()
        settingsScreen()
    }
}
