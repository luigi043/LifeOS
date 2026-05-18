package com.luigi.lifeos.feature.finance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.model.RecurringPeriod
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import com.luigi.lifeos.feature.finance.domain.usecase.AddExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class AddExpenseUiState(
    val amountText: String = "",
    val note: String = "",
    val selectedCategory: ExpenseCategory? = null,
    val categories: List<ExpenseCategory> = emptyList(),
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod = RecurringPeriod.MONTHLY,
    val isSaving: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpense: AddExpenseUseCase,
    repository: FinanceRepository,
) : ViewModel() {

    private val _form = MutableStateFlow(AddExpenseUiState())
    private val _categories = repository.observeCategories()

    val state: StateFlow<AddExpenseUiState> = combine(
        _form, _categories,
    ) { form, categories ->
        form.copy(
            categories       = categories,
            selectedCategory = form.selectedCategory ?: categories.firstOrNull(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AddExpenseUiState())

    // One-shot navigation event — emitted after a successful save
    private val _saveSuccess = Channel<Unit>(Channel.BUFFERED)
    val saveSuccess = _saveSuccess.receiveAsFlow()

    fun onAmountChanged(text: String) {
        // Allow only digits and one decimal separator
        val filtered = text.filter { it.isDigit() || it == '.' }
            .let { if (it.count { c -> c == '.' } > 1) _form.value.amountText else it }
        _form.update { it.copy(amountText = filtered, error = null) }
    }

    fun onNoteChanged(note: String) = _form.update { it.copy(note = note) }

    fun onCategorySelected(category: ExpenseCategory) =
        _form.update { it.copy(selectedCategory = category) }

    fun onRecurringToggled() = _form.update { it.copy(isRecurring = !it.isRecurring) }

    fun onRecurringPeriodSelected(period: RecurringPeriod) =
        _form.update { it.copy(recurringPeriod = period) }

    fun onSave() {
        val current = state.value   // Fix: read merged state so auto-selected category is visible
        val amount = current.amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _form.update { it.copy(error = "Enter a valid amount") }
            return
        }
        val category = current.selectedCategory ?: run {
            _form.update { it.copy(error = "Select a category") }
            return
        }

        _form.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            val now = Instant.now().toEpochMilli()
            addExpense(
                Expense(
                    amountMinor           = (amount * 100).toLong(),
                    currency              = "EUR",
                    categoryId            = category.id,
                    note                  = current.note.trim(),
                    isRecurring           = current.isRecurring,
                    recurringPeriod       = if (current.isRecurring) current.recurringPeriod else null,
                    dateEpochMillis       = now,
                    createdAtEpochMillis  = now,
                )
            )
            _form.update { it.copy(isSaving = false) }
            _saveSuccess.send(Unit)
        }
    }
}
