package com.luigi.lifeos.feature.settings.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * SettingsViewModel — currently exposes a static placeholder state.
 * Replace [SettingsUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        SettingsUiState(tagline = "App settings, themes, notifications, data export."),
    )
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()
}

data class SettingsUiState(
    val tagline: String,
)
