package com.luigi.lifeos.feature.finance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.domain.model.BudgetSummary
import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.model.Income
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import com.luigi.lifeos.feature.finance.domain.usecase.DeleteExpenseUseCase
import com.luigi.lifeos.feature.finance.domain.usecase.GetBudgetSummaryUseCase
import com.luigi.lifeos.feature.finance.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject
import com.luigi.lifeos.feature.finance.domain.usecase.toEpochMillisRange
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.catch

enum class FinanceTab { EXPENSES, INCOME }

data class FinanceUiState(
    // Fix: non-nullable — GetBudgetSummaryUseCase always emits a value (zero-limit when unset)
    val budgetSummary: BudgetSummary = BudgetSummary(0L, 0L, "EUR", emptyList()),
    val expenses: List<Expense> = emptyList(),
    val income: List<Income> = emptyList(),
    val categories: List<ExpenseCategory> = emptyList(),
    val selectedTab: FinanceTab = FinanceTab.EXPENSES,
    val isLoading: Boolean = true,
    val currency: String = "EUR",
    val error: String? = null,
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val getExpenses: GetExpensesUseCase,
    private val getBudgetSummary: GetBudgetSummaryUseCase,
    private val deleteExpense: DeleteExpenseUseCase,
    private val repository: FinanceRepository,
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedTab = MutableStateFlow(FinanceTab.EXPENSES)

    val state: StateFlow<FinanceUiState> = combine(
        getBudgetSummary(YearMonth.now()),
        getExpenses(YearMonth.now()),
        repository.observeIncomeByPeriod(YearMonth.now().toEpochMillisRange().first, YearMonth.now().toEpochMillisRange().second),
        repository.observeCategories(),
        _selectedTab,
    ) { budgetSummary, expenses, income, categories, tab ->
        FinanceUiState(
            budgetSummary = budgetSummary,
            expenses      = expenses,
            income        = income,
            categories    = categories,
            selectedTab   = tab,
            isLoading     = false,
            currency      = budgetSummary.currency,
        )
    }.catch { e ->
        if (e is CancellationException) throw e
        emit(FinanceUiState(isLoading = false, error = "Could not load finance data. Reopen this screen to retry."))
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = FinanceUiState(),
    )

    fun onTabSelected(tab: FinanceTab) {
        _selectedTab.update { tab }
    }

    fun onDeleteExpense(expense: Expense) {
        viewModelScope.launch {
            try { deleteExpense(expense); _error.value = null }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { _error.value = "Could not delete the expense. Please try again." }
        }
    }

    init {
        // Seed default expense categories on first launch (idempotent)
        viewModelScope.launch {
            try { repository.seedDefaultCategoriesIfNeeded() }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { _error.value = "Could not load categories. Reopen Finance to retry." }
        }
    }
}
