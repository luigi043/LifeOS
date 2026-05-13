package com.luigi.lifeos.feature.mentalhealth.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * MentalHealthViewModel — currently exposes a static placeholder state.
 * Replace [MentalHealthUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class MentalHealthViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        MentalHealthUiState(tagline = "Mood, journaling, stability scoring, burnout detection."),
    )
    val state: StateFlow<MentalHealthUiState> = _state.asStateFlow()
}

data class MentalHealthUiState(
    val tagline: String,
)
