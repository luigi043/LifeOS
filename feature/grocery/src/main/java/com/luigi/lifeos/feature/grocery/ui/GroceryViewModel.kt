package com.luigi.lifeos.feature.grocery.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * GroceryViewModel — currently exposes a static placeholder state.
 * Replace [GroceryUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class GroceryViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        GroceryUiState(tagline = "Adaptive grocery lists, pantry, cost optimisation."),
    )
    val state: StateFlow<GroceryUiState> = _state.asStateFlow()
}

data class GroceryUiState(
    val tagline: String,
)
