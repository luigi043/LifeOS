package com.luigi.lifeos.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.MaterialTheme

/**
 * Settings screen.
 *
 * App settings, themes, notifications, data export.
 *
 * Currently a placeholder — wire to real domain repos as the feature is built out.
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize().verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Text("Appearance", style = MaterialTheme.typography.titleLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Dark mode")
            Switch(checked = state.darkMode, onCheckedChange = viewModel::onDarkModeChanged)
        }
        Text("Currency for new expenses and budget totals")
        listOf("EUR", "USD", "GBP", "BRL").forEach { code ->
            FilterChip(selected = state.baseCurrency == code, onClick = { viewModel.onCurrencyChanged(code) }, label = { Text(code) })
        }
        Text("Existing expenses keep their original currency. No currency conversion is performed.")
        Text("Local storage only. Cloud sync, reminders and data export are in development.")
    }
}
