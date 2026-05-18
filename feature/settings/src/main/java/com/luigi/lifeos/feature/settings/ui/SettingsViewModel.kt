package com.luigi.lifeos.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsViewModel — manages user preferences like theme and currency.
 * It observes [UserPreferences] from the DataStore and exposes them as [SettingsUiState].
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    val state: StateFlow<SettingsUiState> = combine(
        userPreferences.darkMode,
        userPreferences.baseCurrency
    ) { darkMode, currency ->
        SettingsUiState(
            darkMode = darkMode ?: false,
            baseCurrency = currency,
            tagline = "App settings, themes, notifications, and data export."
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(tagline = "App settings, themes, notifications, and data export.")
    )

    fun onDarkModeChanged(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setDarkMode(enabled)
        }
    }

    fun onCurrencyChanged(code: String) {
        viewModelScope.launch {
            userPreferences.setBaseCurrency(code)
        }
    }
}

data class SettingsUiState(
    val darkMode: Boolean = false,
    val baseCurrency: String = "EUR",
    val tagline: String = "",
)
