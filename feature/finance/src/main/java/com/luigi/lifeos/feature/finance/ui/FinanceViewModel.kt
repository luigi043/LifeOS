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
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val getExpenses: GetExpensesUseCase,
    private val getBudgetSummary: GetBudgetSummaryUseCase,
    private val deleteExpense: DeleteExpenseUseCase,
    private val repository: FinanceRepository,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(FinanceTab.EXPENSES)

    val state: StateFlow<FinanceUiState> = combine(
        getBudgetSummary(YearMonth.now()),
        getExpenses(YearMonth.now()),
        repository.observeIncome(),
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
    }.stateIn(
        scope        = viewModelScope,
        started      = SharingStarted.WhileSubscribed(5_000),
        initialValue = FinanceUiState(),
    )

    fun onTabSelected(tab: FinanceTab) {
        _selectedTab.update { tab }
    }

    fun onDeleteExpense(expense: Expense) {
        viewModelScope.launch { deleteExpense(expense) }
    }

    init {
        // Seed default expense categories on first launch (idempotent)
        viewModelScope.launch { repository.seedDefaultCategoriesIfNeeded() }
    }
}
