package com.luigi.lifeos.feature.analytics.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * AnalyticsViewModel — currently exposes a static placeholder state.
 * Replace [AnalyticsUiState] with concrete fields when the feature is implemented.
 */
@HiltViewModel
class AnalyticsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(
        AnalyticsUiState(tagline = "Insights across systems — correlations, trends, AI insights."),
    )
    val state: StateFlow<AnalyticsUiState> = _state.asStateFlow()
}

data class AnalyticsUiState(
    val tagline: String,
)
