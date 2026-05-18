package com.luigi.lifeos.feature.finance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.model.RecurringPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()

    // Navigate back after successful save
    LaunchedEffect(Unit) {
        viewModel.saveSuccess.collect { onBack() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // ── Amount ──────────────────────────────────────────────────────
            Text("Amount", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value         = state.amountText,
                onValueChange = viewModel::onAmountChanged,
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("0.00") },
                prefix        = { Text("€  ", fontWeight = FontWeight.Bold) },
                isError       = state.error != null,
                supportingText = state.error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine    = true,
                textStyle     = MaterialTheme.typography.headlineMedium,
            )

            // ── Category ────────────────────────────────────────────────────
            Text("Category", style = MaterialTheme.typography.labelLarge)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding        = PaddingValues(horizontal = 0.dp),
            ) {
                items(state.categories) { category ->
                    CategoryChip(
                        category   = category,
                        selected   = category.id == state.selectedCategory?.id,
                        onClick    = { viewModel.onCategorySelected(category) },
                    )
                }
            }

            // ── Note ────────────────────────────────────────────────────────
            Text("Note", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value         = state.note,
                onValueChange = viewModel::onNoteChanged,
                modifier      = Modifier.fillMaxWidth(),
                placeholder   = { Text("What was this for?") },
                maxLines      = 2,
            )

            // ── Recurring ───────────────────────────────────────────────────
            Row(
                modifier             = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment    = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Recurring", style = MaterialTheme.typography.labelLarge)
                    Text(
                        "Repeats automatically",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked         = state.isRecurring,
                    onCheckedChange = { viewModel.onRecurringToggled() },
                )
            }

            if (state.isRecurring) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RecurringPeriod.entries.forEach { period ->
                        FilterChip(
                            selected = period == state.recurringPeriod,
                            onClick  = { viewModel.onRecurringPeriodSelected(period) },
                            label    = { Text(period.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Save ────────────────────────────────────────────────────────
            Button(
                onClick  = viewModel::onSave,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled  = !state.isSaving,
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save Expense", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    category: ExpenseCategory,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = if (selected) parseHexColor(category.colorHex).copy(alpha = 0.2f)
                  else MaterialTheme.colorScheme.surfaceVariant
    val borderColor = if (selected) parseHexColor(category.colorHex)
                      else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))  // Fix: apply border
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(parseHexColor(category.colorHex)),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text  = category.name,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun parseHexColor(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: Exception) {
    Color(0xFF6750A4)
}
