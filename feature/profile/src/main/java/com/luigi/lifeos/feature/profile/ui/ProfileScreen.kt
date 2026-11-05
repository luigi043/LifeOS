package com.luigi.lifeos.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * Profile screen.
 *
 * Account, goals, body metrics, preferences.
 *
 * Currently a placeholder — wire to real domain repos as the feature is built out.
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize().verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineLarge)
        val profile = state
        if (profile == null) {
            Text("Loading your local profile...")
        } else {
            Text(profile.displayName, style = MaterialTheme.typography.headlineMedium)
            Text("Goal: " + profile.fitnessGoal.name.lowercase().replace('_', ' '))
            Text("Activity: " + profile.activityLevel.name.lowercase())
            Text("Diet: " + profile.dietaryPreference.name.lowercase())
            profile.age?.let { Text("Age: $it") }
            profile.heightCm?.let { Text("Height: $it cm") }
            profile.weightKg?.let { Text("Weight: $it kg") }
            profile.goalWeightKg?.let { Text("Goal weight: $it kg") }
            Text("Currency: " + profile.preferredCurrency)
            profile.monthlyBudgetMinor?.let { Text("Monthly budget: " + java.math.BigDecimal.valueOf(it, 2).toPlainString() + " " + profile.preferredCurrency) }
            Text("Stored on this device. Profile editing is in development.")
        }
    }
}
