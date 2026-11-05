package com.luigi.lifeos.feature.onboarding.ui

import com.luigi.lifeos.core.domain.model.Money
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
            OnboardingStep.PROFILE -> displayName.isNotBlank() && (ageText.isBlank() || ageText.toIntOrNull()?.let { it in 1..120 } == true)
            OnboardingStep.BODY -> listOf(heightCmText, weightKgText, goalWeightKgText).all {
                it.isBlank() || (Regex("[0-9]+(?:\\.[0-9]+)?").matches(it) && it.toDoubleOrNull()?.let { value -> value.isFinite() && value > 0 && value <= 1000 } == true)
            }
            OnboardingStep.BUDGET -> listOf(monthlyBudgetText, foodBudgetText).all {
                it.isBlank() || Money.parseMinorUnits(it) != null
            } && (foodBudgetText.isBlank() || monthlyBudgetText.isBlank() ||
                (Money.parseMinorUnits(foodBudgetText) ?: Long.MAX_VALUE) <= (Money.parseMinorUnits(monthlyBudgetText) ?: 0))
            OnboardingStep.GOALS,
            OnboardingStep.REVIEW -> true
        }

    val validationMessage: String?
        get() = if (canAdvance) null else when (step) {
            OnboardingStep.PROFILE -> "Enter your name and an age from 1 to 120, or leave age blank."
            OnboardingStep.BODY -> "Enter positive measurements up to 1000, or leave them blank."
            OnboardingStep.BUDGET -> "Use up to two decimal places. Food budget must not exceed monthly budget."
            else -> null
        }

    val progressFraction: Float
        get() = (step.ordinal + 1f) / OnboardingStep.entries.size
}
