package com.luigi.lifeos.feature.today.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luigi.lifeos.core.designsystem.component.LifeOsCard

/**
 * Daily dashboard — the headline screen.
 *
 * Per spec, this screen is the user's *first impression every day*:
 *   "Today" — not "Finance", not "Workout". Tiles aggregate state from
 *   every feature module via cross-feature use cases. Placeholder values
 *   for now — wire to real repositories once features port over.
 */
@Composable
fun TodayScreen(onNavigate: (String) -> Unit, viewModel: TodayViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Today",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = state.greeting,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        // Two-up tiles
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TodayTile("Budget left", state.budgetRemaining, Modifier.weight(1f))
            TodayTile("Calories", state.caloriesText, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TodayTile("Workout", state.workoutName, Modifier.weight(1f))
            TodayTile("Hydration", state.hydration, Modifier.weight(1f))
        }

        // Headline tile — Stability Score (cross-system metric from :core:domain)
        LifeOsCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text("Lifestyle tracking", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = "In development",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(state.stabilityHint, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text("Explore LifeOS", style = MaterialTheme.typography.titleLarge)
        listOf("Finance" to "finance", "Nutrition" to "nutrition", "Workout" to "workout",
            "Grocery" to "grocery", "Mental wellness" to "mentalhealth", "Habits" to "habits",
            "Analytics" to "analytics", "Settings" to "settings").forEach { (label, route) ->
            OutlinedButton(onClick = { onNavigate(route) }, modifier = Modifier.fillMaxWidth()) { Text(label) }
        }

    }
}

@Composable
private fun TodayTile(label: String, value: String, modifier: Modifier = Modifier) {
    LifeOsCard(modifier = modifier) {
        Column {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium)
        }
    }
}
