package com.luigi.lifeos.feature.onboarding.ui

import com.luigi.lifeos.core.domain.model.ActivityLevel
import com.luigi.lifeos.core.domain.model.DietaryPreference
import com.luigi.lifeos.core.domain.model.FitnessGoal

/**
 * Five-step onboarding flow. Steps are ordered enums so the ViewModel
 * can do "step + 1" / "step - 1" without a separate state machine.
 *
 * Why five steps: spec lists ~10 fields, but stacking them into one
 * monolithic form would hurt completion. Splitting by *theme* (profile,
 * body, goals, money, prefs) keeps each step <60s.
 */
enum class OnboardingStep {
    WELCOME,
    PROFILE,        // name, age
    BODY,           // height, weight, goal weight
    GOALS,          // fitness goal, activity level, dietary preference
    BUDGET,         // monthly budget, food budget, currency
    REVIEW,         // confirm + save
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.WELCOME,
    // Profile
    val displayName: String = "",
    val ageText: String = "",
    // Body
    val heightCmText: String = "",
    val weightKgText: String = "",
    val goalWeightKgText: String = "",
    // Goals
    val fitnessGoal: FitnessGoal = FitnessGoal.GENERAL_HEALTH,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val dietaryPreference: DietaryPreference = DietaryPreference.OMNIVORE,
    // Budget
    val monthlyBudgetText: String = "",
    val foodBudgetText: String = "",
    val currency: String = "EUR",
    // Process state
    val isSaving: Boolean = false,
    val error: String? = null,
) {
    val canAdvance: Boolean
        get() = when (step) {
            OnboardingStep.WELCOME -> true
            OnboardingStep.PROFILE -> displayName.isNotBlank()
            OnboardingStep.BODY,
            OnboardingStep.GOALS,
            OnboardingStep.BUDGET,
            OnboardingStep.REVIEW -> true
        }

    val progressFraction: Float
        get() = (step.ordinal + 1f) / OnboardingStep.entries.size
}
