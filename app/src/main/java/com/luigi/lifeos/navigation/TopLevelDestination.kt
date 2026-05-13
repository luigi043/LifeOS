package com.luigi.lifeos.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.ui.graphics.vector.ImageVector
import com.luigi.lifeos.feature.analytics.navigation.AnalyticsRoute
import com.luigi.lifeos.feature.nutrition.navigation.NutritionRoute
import com.luigi.lifeos.feature.profile.navigation.ProfileRoute
import com.luigi.lifeos.feature.today.navigation.TodayRoute
import com.luigi.lifeos.feature.workout.navigation.WorkoutRoute

/**
 * Top-level destinations shown in the bottom nav.
 *
 * Per spec: avoid module-shaped tabs ("Finance", "Workout"). Instead, the
 * bottom nav is organised around *what the user wants right now*:
 *
 *  - Today      → the unified dashboard (feature:today)
 *  - Plan       → meal plans, workout plans, grocery (composite future surface)
 *  - Progress   → tracking over time
 *  - Insights   → analytics + AI insights (feature:analytics)
 *  - Profile    → settings, profile, goals (feature:profile)
 *
 * Plan and Progress are placeholders pointing to feature:today for now —
 * the corresponding composite screens will replace them in Phase 2.
 */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    TODAY(
        route = TodayRoute,
        label = "Today",
        selectedIcon = Icons.Rounded.Today,
        unselectedIcon = Icons.Outlined.Today,
    ),
    // Plan: routed to :feature:nutrition for now until a composite Plan surface
    // is built in Phase 2 (meal plan + workout plan + grocery roll-up).
    PLAN(
        route = NutritionRoute,
        label = "Plan",
        selectedIcon = Icons.Rounded.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
    ),
    // Progress: routed to :feature:workout for now. Will become a composite
    // surface (weight trends + workout streaks + budget streaks) in Phase 2.
    PROGRESS(
        route = WorkoutRoute,
        label = "Progress",
        selectedIcon = Icons.Rounded.TrendingUp,
        unselectedIcon = Icons.Outlined.TrendingUp,
    ),
    INSIGHTS(
        route = AnalyticsRoute,
        label = "Insights",
        selectedIcon = Icons.Rounded.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
    ),
    PROFILE(
        route = ProfileRoute,
        label = "Profile",
        selectedIcon = Icons.Rounded.Person,
        unselectedIcon = Icons.Outlined.Person,
    ),
}
