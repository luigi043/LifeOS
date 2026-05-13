package com.luigi.lifeos.feature.profile.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ProfileViewModel — currently exposes a static placeholder state.
 * Replace [ProfileUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        ProfileUiState(tagline = "Account, goals, body metrics, preferences."),
    )
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()
}

data class ProfileUiState(
    val tagline: String,
)
