package com.luigi.lifeos.feature.habits.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * HabitsViewModel — currently exposes a static placeholder state.
 * Replace [HabitsUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class HabitsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        HabitsUiState(tagline = "Habit tracking, streaks, consistency."),
    )
    val state: StateFlow<HabitsUiState> = _state.asStateFlow()
}

data class HabitsUiState(
    val tagline: String,
)
