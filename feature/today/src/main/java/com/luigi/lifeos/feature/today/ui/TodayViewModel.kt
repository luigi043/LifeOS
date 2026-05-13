package com.luigi.lifeos.feature.today.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * TodayViewModel aggregates state from every feature for the dashboard.
 *
 * Currently emits hard-coded placeholders so the screen renders end-to-end.
 * Replace with use-case injections as features come online:
 *
 *   class TodayViewModel @Inject constructor(
 *     getStabilityScore: GetTodayStabilityScoreUseCase,
 *     getBudgetSnapshot: GetTodayBudgetSnapshotUseCase,
 *     getNutritionToday: GetTodayNutritionUseCase,
 *     getPlannedWorkout: GetPlannedWorkoutUseCase,
 *     getHydration: GetTodayHydrationUseCase,
 *   ) : ViewModel() { ... combine() into TodayUiState ... }
 */
@HiltViewModel
class TodayViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        TodayUiState(
            greeting = "Welcome back.",
            stabilityScore = 0,
            stabilityHint = "Log your day to start tracking stability.",
            budgetRemaining = "—",
            caloriesText = "—",
            workoutName = "Rest day",
            hydration = "0 L",
        ),
    )
    val state: StateFlow<TodayUiState> = _state.asStateFlow()
}

data class TodayUiState(
    val greeting: String,
    val stabilityScore: Int,      // 0..100, from StabilityScore.calculate
    val stabilityHint: String,
    val budgetRemaining: String,  // formatted via :core:common formatters
    val caloriesText: String,     // "1850 / 2400"
    val workoutName: String,
    val hydration: String,
)
