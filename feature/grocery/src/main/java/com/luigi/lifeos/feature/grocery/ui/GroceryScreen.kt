package com.luigi.lifeos.feature.grocery.ui

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
 * Grocery screen.
 *
 * Adaptive grocery lists, pantry, cost optimisation.
 *
 * Currently a placeholder — wire to real domain repos as the feature is built out.
 */
@Composable
fun GroceryScreen(
    viewModel: GroceryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Grocery", style = MaterialTheme.typography.headlineLarge)
        if (state.isLoading) {
            Text("Loading…", style = MaterialTheme.typography.bodyMedium)
        } else {
            val total = state.pendingEstimatedTotal
            Text(
                "${state.pendingItems.size} item(s) pending" +
                    if (total != null) " · est. €${"%.2f".format(total)}" else "",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
