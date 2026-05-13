package com.luigi.lifeos.feature.mentalhealth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.MaterialTheme

/**
 * MentalHealth screen.
 *
 * Mood, journaling, stability scoring, burnout detection.
 *
 * Currently a placeholder — wire to real domain repos as the feature is built out.
 */
@Composable
fun MentalHealthScreen(
    viewModel: MentalHealthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("MentalHealth", style = MaterialTheme.typography.headlineLarge)
        Text(state.tagline, style = MaterialTheme.typography.bodyMedium)
    }
}
