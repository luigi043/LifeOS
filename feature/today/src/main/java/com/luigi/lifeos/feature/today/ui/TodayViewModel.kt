package com.luigi.lifeos.feature.today.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.datastore.UserPreferences
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import com.luigi.lifeos.core.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.CancellationException
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(
    profiles: UserProfileRepository,
    finance: FinanceRepository,
    preferences: UserPreferences,
) : ViewModel() {
    val state = combine(profiles.observe(), finance.observeExpenses(), preferences.baseCurrency) { profile, expenses, currency ->
        val month = YearMonth.now()
        val spent = expenses.filter { it.currency == currency && YearMonth.from(Instant.ofEpochMilli(it.dateEpochMillis).atZone(ZoneId.systemDefault())) == month }.sumOf { it.amountMinor }
        val budget = profile?.takeIf { it.preferredCurrency == currency }?.monthlyBudgetMinor
        val formatter = NumberFormat.getCurrencyInstance(Locale.ENGLISH).apply { this.currency = Currency.getInstance(currency) }
        TodayUiState(
            greeting = profile?.let { "Welcome, " + it.displayName + "." } ?: "Welcome back.",
            budgetRemaining = budget?.let { formatter.format((it - spent) / 100.0) } ?: "Not set",
        )
    }.catch { e ->
        if (e is CancellationException) throw e
        emit(TodayUiState(greeting = "Could not load your overview. Reopen Today to retry."))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayUiState())
}

data class TodayUiState(
    val greeting: String = "Loading your overview...",
    val stabilityHint: String = "Finance and your local profile are available. Lifestyle tracking and stability scoring are not implemented yet.",
    val budgetRemaining: String = "Not set",
    val caloriesText: String = "Not tracked",
    val workoutName: String = "Not tracked",
    val hydration: String = "Not tracked",
)
