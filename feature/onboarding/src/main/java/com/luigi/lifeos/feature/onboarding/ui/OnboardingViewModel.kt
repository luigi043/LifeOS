package com.luigi.lifeos.feature.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.datastore.UserPreferences
import com.luigi.lifeos.core.domain.model.ActivityLevel
import com.luigi.lifeos.core.domain.model.DietaryPreference
import com.luigi.lifeos.core.domain.model.FitnessGoal
import com.luigi.lifeos.core.domain.model.UserProfile
import com.luigi.lifeos.core.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Drives the onboarding flow.
 *
 * Persists the result via [UserProfileRepository] (Room) and flips the
 * `onboardingDone` flag in [UserPreferences] (DataStore) as the final
 * commit. The flag is what the app reads at cold start to decide the
 * NavHost startDestination — so it MUST be set last, after the profile
 * is safely on disk, otherwise a crash mid-save would skip onboarding
 * without a saved profile.
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _events = MutableStateFlow<OnboardingEvent?>(null)
    val events: StateFlow<OnboardingEvent?> = _events.asStateFlow()

    // ---- Field updates ----

    fun onDisplayNameChange(value: String) = _state.update { it.copy(displayName = value) }
    fun onAgeChange(value: String) = _state.update { it.copy(ageText = value.filter { c -> c.isDigit() }) }
    fun onHeightChange(value: String) = _state.update { it.copy(heightCmText = value.filterNumeric()) }
    fun onWeightChange(value: String) = _state.update { it.copy(weightKgText = value.filterNumeric()) }
    fun onGoalWeightChange(value: String) = _state.update { it.copy(goalWeightKgText = value.filterNumeric()) }
    fun onFitnessGoalChange(goal: FitnessGoal) = _state.update { it.copy(fitnessGoal = goal) }
    fun onActivityLevelChange(level: ActivityLevel) = _state.update { it.copy(activityLevel = level) }
    fun onDietaryPreferenceChange(pref: DietaryPreference) = _state.update { it.copy(dietaryPreference = pref) }
    fun onMonthlyBudgetChange(value: String) = _state.update { it.copy(monthlyBudgetText = value.filterNumeric()) }
    fun onFoodBudgetChange(value: String) = _state.update { it.copy(foodBudgetText = value.filterNumeric()) }
    fun onCurrencyChange(code: String) = _state.update { it.copy(currency = code) }

    // ---- Navigation ----

    /** Next step, or finish if on the review screen. */
    fun next() {
        val current = _state.value
        if (!current.canAdvance) return
        if (current.step == OnboardingStep.REVIEW) {
            finish()
            return
        }
        val nextOrdinal = (current.step.ordinal + 1).coerceAtMost(OnboardingStep.entries.lastIndex)
        _state.update { it.copy(step = OnboardingStep.entries[nextOrdinal]) }
    }

    /** Back, or no-op if on first step. */
    fun back() {
        val prevOrdinal = (_state.value.step.ordinal - 1).coerceAtLeast(0)
        _state.update { it.copy(step = OnboardingStep.entries[prevOrdinal]) }
    }

    fun consumeEvent() { _events.value = null }

    // ---- Commit ----

    private fun finish() {
        val s = _state.value
        if (s.isSaving) return
        _state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            runCatching {
                val profile = UserProfile(
                    displayName = s.displayName.trim().ifBlank { "You" },
                    age = s.ageText.toIntOrNull(),
                    heightCm = s.heightCmText.toDoubleOrNull(),
                    weightKg = s.weightKgText.toDoubleOrNull(),
                    goalWeightKg = s.goalWeightKgText.toDoubleOrNull(),
                    fitnessGoal = s.fitnessGoal,
                    activityLevel = s.activityLevel,
                    dietaryPreference = s.dietaryPreference,
                    monthlyBudgetMinor = s.monthlyBudgetText.toMinorUnitsOrNull(),
                    foodBudgetMinor = s.foodBudgetText.toMinorUnitsOrNull(),
                    preferredCurrency = s.currency,
                    createdAtEpochMillis = System.currentTimeMillis(),
                )
                userProfileRepository.save(profile)
                // Flip the flag LAST — see kdoc.
                userPreferences.markOnboardingDone()
                if (s.currency.isNotBlank()) {
                    userPreferences.setBaseCurrency(s.currency)
                }
            }.fold(
                onSuccess = {
                    _state.update { it.copy(isSaving = false) }
                    _events.value = OnboardingEvent.Finished
                },
                onFailure = { e ->
                    _state.update { it.copy(isSaving = false, error = e.message ?: "Couldn't save profile.") }
                },
            )
        }
    }
}

/** One-shot UI events. Consumed by the screen and cleared via [OnboardingViewModel.consumeEvent]. */
sealed interface OnboardingEvent {
    data object Finished : OnboardingEvent
}

private fun String.filterNumeric(): String =
    filter { it.isDigit() || it == '.' || it == ',' }.replace(',', '.')

/** Parse a major-units string (e.g. "1200.50") into integer minor units. */
private fun String.toMinorUnitsOrNull(): Long? =
    toDoubleOrNull()?.let { (it * 100).toLong() }
