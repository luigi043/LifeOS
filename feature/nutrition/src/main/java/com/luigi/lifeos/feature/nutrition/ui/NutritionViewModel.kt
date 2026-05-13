package com.luigi.lifeos.feature.nutrition.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * NutritionViewModel — currently exposes a static placeholder state.
 * Replace [NutritionUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class NutritionViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        NutritionUiState(tagline = "Recipes, meal planning, calorie + macro tracking, hydration."),
    )
    val state: StateFlow<NutritionUiState> = _state.asStateFlow()
}

data class NutritionUiState(
    val tagline: String,
)
