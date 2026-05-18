package com.luigi.lifeos.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.luigi.lifeos.feature.onboarding.ui.OnboardingScreen

/**
 * Navigation entry point for onboarding.
 *
 * Onboarding is a *destination*, not a top-level tab — the app decides at
 * cold start whether to make this the start destination (based on
 * UserPreferences.onboardingDone) or to skip straight to Today.
 *
 * Once the user completes onboarding, we call [onFinished] which the app
 * uses to navigate to Today and pop onboarding off the back stack.
 */
const val OnboardingRoute = "onboarding"

fun NavGraphBuilder.onboardingScreen(onFinished: () -> Unit) {
    composable(route = OnboardingRoute) {
        OnboardingScreen(onFinished = onFinished)
    }
}
