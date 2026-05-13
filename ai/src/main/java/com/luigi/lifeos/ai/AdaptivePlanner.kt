package com.luigi.lifeos.ai

import com.luigi.lifeos.core.domain.model.Money

/**
 * Contract for the adaptive weekly planner from the spec.
 *
 * Inputs: user context (budget, time, energy, goals, pantry).
 * Outputs: a coherent plan across nutrition / workout / grocery / spending.
 *
 * Implementation lives behind this interface so the feature layer is
 * insulated from whether plans are produced by:
 *   - on-device heuristics,
 *   - a remote LLM (OpenAI, Anthropic),
 *   - or a hybrid (LLM proposes, heuristic verifies cost / macros).
 */
interface AdaptivePlanner {
    suspend fun plan(input: PlanInput): WeeklyPlan
}

data class PlanInput(
    val weeklyFoodBudget: Money,
    val dailyCalorieTarget: Int,
    val dailyProteinTargetGrams: Int,
    val availableTimePerDayMinutes: Int,
    val energyLevel: EnergyLevel,
    val pantry: List<String>, // ingredient names already owned
)

enum class EnergyLevel { LOW, MEDIUM, HIGH }

data class WeeklyPlan(
    val mealPlanIds: List<String>,
    val workoutPlanIds: List<String>,
    val groceryItems: List<String>,
    val estimatedSpend: Money,
    val notes: List<String>,
)
