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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * SettingsViewModel — manages user preferences like theme and currency.
 * It observes [UserPreferences] from the DataStore and exposes them as [SettingsUiState].
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val error = MutableStateFlow<String?>(null)

    val state: StateFlow<SettingsUiState> = combine(
        userPreferences.darkMode,
        userPreferences.baseCurrency,
        error,
    ) { darkMode, currency, error ->
        SettingsUiState(
            darkMode = darkMode ?: false,
            baseCurrency = currency,
            error = error,
            tagline = "App settings, themes, notifications, and data export."
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(tagline = "App settings, themes, notifications, and data export.")
    )

    fun onDarkModeChanged(enabled: Boolean) {
        viewModelScope.launch {
            savePreference { userPreferences.setDarkMode(enabled) }
        }
    }

    fun onCurrencyChanged(code: String) {
        viewModelScope.launch {
            savePreference { userPreferences.setBaseCurrency(code) }
        }
    }
    private suspend fun savePreference(action: suspend () -> Unit) {
        try { action(); error.value = null }
        catch (e: CancellationException) { throw e }
        catch (_: Exception) { error.value = "Could not save preferences. Please try again." }
    }
}

data class SettingsUiState(
    val error: String? = null,
    val darkMode: Boolean = false,
    val baseCurrency: String = "EUR",
    val tagline: String = "",
)
