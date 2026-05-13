package com.luigi.lifeos.feature.finance.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * FinanceViewModel — currently exposes a static placeholder state.
 * Replace [FinanceUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class FinanceViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        FinanceUiState(tagline = "Expenses, income, budgets, recurring bills, subscriptions, analytics."),
    )
    val state: StateFlow<FinanceUiState> = _state.asStateFlow()
}

data class FinanceUiState(
    val tagline: String,
)
