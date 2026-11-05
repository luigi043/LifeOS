package com.luigi.lifeos.feature.finance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luigi.lifeos.core.domain.model.BudgetSummary
import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.model.Income
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    onNavigateToAddExpense: () -> Unit,
    viewModel: FinanceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val actionError by viewModel.error.collectAsState()
    var pendingDelete by remember { mutableStateOf<Expense?>(null) }
    pendingDelete?.let { expense ->
        AlertDialog(onDismissRequest = { pendingDelete = null },
            title = { Text("Delete expense?") },
            text = { Text("This removes the saved expense from this device.") },
            confirmButton = { TextButton(onClick = { viewModel.onDeleteExpense(expense); pendingDelete = null }) { Text("Delete") } },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } })
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Finance") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddExpense) {
                Icon(Icons.Default.Add, contentDescription = "Add expense")
            }
        },
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            item { (actionError ?: state.error)?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp)) } }
            item {
                BudgetCard(
                    summary  = state.budgetSummary,
                    currency = state.currency,
                    modifier = Modifier.padding(16.dp),
                )
            }

            item {
                TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                    FinanceTab.entries.forEach { tab ->
                        Tab(
                            selected = tab == state.selectedTab,
                            onClick  = { viewModel.onTabSelected(tab) },
                            text     = {
                                Text(tab.name.lowercase().replaceFirstChar { it.uppercase() })
                            },
                        )
                    }
                }
            }

            when (state.selectedTab) {
                FinanceTab.EXPENSES -> {
                    if (state.expenses.isEmpty()) {
                        item { EmptyState("No expenses this month.\nTap + to add one.") }
                    } else {
                        items(state.expenses, key = { it.id }) { expense ->
                            ExpenseItem(
                                expense  = expense,
                                category = state.categories.find { it.id == expense.categoryId },
                                currency = expense.currency,
                                onDelete = { pendingDelete = expense },
                            )
                            HorizontalDivider(modifier = Modifier.padding(start = 64.dp))
                        }
                    }
                }

                FinanceTab.INCOME -> {
                    if (state.income.isEmpty()) {
                        item { EmptyState("No income logged this month. Income entry is in development.") }
                    } else {
                        items(state.income, key = { it.id }) { income ->
                            IncomeItem(income = income, currency = income.currency)
                            HorizontalDivider(modifier = Modifier.padding(start = 64.dp))
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(88.dp)) }
        }
    }
}

// ── Budget Card ──────────────────────────────────────────────────────────────

@Composable
private fun BudgetCard(
    summary: BudgetSummary,      // Fix: non-nullable — ViewModel always provides a value
    currency: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier  = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Totals in $currency only. Other currencies are listed separately.", style = MaterialTheme.typography.bodySmall)
            Text(
                "This Month",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))

            if (summary.totalBudgetMinor == 0L) {   // Fix: remove null check
                Text("No budget set", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Spent ${formatMoney(summary.totalSpentMinor, currency)}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                val overBudget = summary.isOverBudget
                val highSpend  = summary.usedFraction > 0.8f

                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Bottom,
                ) {
                    Column {
                        Text(
                            formatMoney(if (overBudget) -summary.remainingMinor else summary.remainingMinor, currency),
                            style      = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color      = if (overBudget) MaterialTheme.colorScheme.error
                                         else MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            if (overBudget) "over budget" else "remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        "of ${formatMoney(summary.totalBudgetMinor, currency)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress   = { summary.usedFraction },
                    modifier   = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color      = when {
                        overBudget -> MaterialTheme.colorScheme.error
                        highSpend  -> MaterialTheme.colorScheme.tertiary
                        else       -> MaterialTheme.colorScheme.primary
                    },
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap  = StrokeCap.Round,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Spent ${formatMoney(summary.totalSpentMinor, currency)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// ── Expense Item ─────────────────────────────────────────────────────────────

@Composable
private fun ExpenseItem(
    expense: Expense,
    category: ExpenseCategory?,
    currency: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val dotColor = parseHexColorSafe(category?.colorHex ?: "#607D8B")
        Box(
            modifier         = Modifier.size(40.dp).clip(CircleShape)
                .background(dotColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(dotColor))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                category?.name ?: "Unknown",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
            if (expense.note.isNotBlank()) {
                Text(
                    expense.note,
                    style   = MaterialTheme.typography.bodySmall,
                    color   = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            Text(
                formatDate(expense.dateEpochMillis),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            "−${formatMoney(expense.amountMinor, currency)}",
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.error,
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete expense " + expense.note.ifBlank { category?.name ?: "Unknown" })
        }
    }
}

// ── Income Item ───────────────────────────────────────────────────────────────

@Composable
private fun IncomeItem(income: Income, currency: String, modifier: Modifier = Modifier) {
    Row(
        modifier          = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier         = Modifier.size(40.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text("€", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(income.source, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium)
            Text(formatDate(income.dateEpochMillis),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Text(
            "+${formatMoney(income.amountMinor, currency)}",
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = Color(0xFF2E7D32),
        )
    }
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier         = Modifier.fillMaxWidth().padding(48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun formatMoney(minorUnits: Long, currency: String): String {
    val major = minorUnits / 100.0
    return NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        this.currency = java.util.Currency.getInstance(currency)
    }.format(major)
}

private val DATE_FMT = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())

private fun formatDate(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).format(DATE_FMT)

private fun parseHexColorSafe(hex: String): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (_: Exception) {
    Color(0xFF607D8B)
}
