package com.luigi.lifeos.feature.workout.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * WorkoutViewModel — currently exposes a static placeholder state.
 * Replace [WorkoutUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class WorkoutViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        WorkoutUiState(tagline = "Workout planning and exercise logging are in development. No workouts are recorded yet."),
    )
    val state: StateFlow<WorkoutUiState> = _state.asStateFlow()
}

data class WorkoutUiState(
    val tagline: String,
)
