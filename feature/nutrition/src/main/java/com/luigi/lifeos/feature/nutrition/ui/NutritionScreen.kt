package com.luigi.lifeos.feature.nutrition.ui

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
 * Nutrition screen.
 *
 * Recipes, meal planning, calorie + macro tracking, hydration.
 *
 * Currently a placeholder — wire to real domain repos as the feature is built out.
 */
@Composable
fun NutritionScreen(
    viewModel: NutritionViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Nutrition", style = MaterialTheme.typography.headlineLarge)
        if (state.isLoading) {
            Text("Loading…", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text(
                "${state.recipes.size} recipe(s) · tap to manage",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
