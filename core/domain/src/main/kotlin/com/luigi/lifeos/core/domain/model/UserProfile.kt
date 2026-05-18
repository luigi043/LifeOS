package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Foundational user model captured during onboarding and referenced by
 * every feature (finance budgets, calorie targets, workout difficulty…).
 *
 * Kept intentionally permissive — many fields are nullable because we let
 * users complete onboarding without filling everything in. Defaults are
 * applied at the consumer (e.g. nutrition assumes 2000 kcal if missing).
 *
 * The whole object is treated as a *singleton* row (id = 1) in the local DB.
 * Multi-account / household support is post-Phase-5 and not modelled yet.
 */
@Serializable
data class UserProfile(
    val id: Long = SINGLETON_ID,
    val displayName: String,
    val age: Int? = null,
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val goalWeightKg: Double? = null,
    val fitnessGoal: FitnessGoal = FitnessGoal.GENERAL_HEALTH,
    val activityLevel: ActivityLevel = ActivityLevel.MODERATE,
    val dietaryPreference: DietaryPreference = DietaryPreference.OMNIVORE,
    val monthlyIncomeMinor: Long? = null,
    val monthlyBudgetMinor: Long? = null,
    val foodBudgetMinor: Long? = null,
    val preferredCurrency: String = "EUR",
    val createdAtEpochMillis: Long,
) {
    companion object {
        const val SINGLETON_ID: Long = 1L
    }
}

@Serializable
enum class FitnessGoal {
    LOSE_WEIGHT,
    GAIN_MUSCLE,
    MAINTAIN,
    GENERAL_HEALTH,
}

@Serializable
enum class ActivityLevel {
    SEDENTARY,    // desk work, no exercise
    LIGHT,        // 1-2 workouts / week
    MODERATE,     // 3-4 workouts / week
    HIGH,         // 5+ workouts / week
    ATHLETE,      // 2-a-days, training-driven
}

@Serializable
enum class DietaryPreference {
    OMNIVORE,
    PESCATARIAN,
    VEGETARIAN,
    VEGAN,
    KETO,
    MEDITERRANEAN,
}
