package com.luigi.lifeos.feature.finance.domain.usecase

import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: FinanceRepository,
) {
    suspend operator fun invoke(expense: Expense) =
        repository.deleteExpense(expense)
}
