package com.luigi.lifeos.feature.finance.domain.usecase

import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

/**
 * Returns a [Flow] of expenses for a given calendar month.
 * Defaults to the current month if [yearMonth] is omitted.
 */
class GetExpensesUseCase @Inject constructor(
    private val repository: FinanceRepository,
) {
    operator fun invoke(yearMonth: YearMonth = YearMonth.now()): Flow<List<Expense>> {
        val (from, to) = yearMonth.toEpochMillisRange()
        return repository.observeExpensesByPeriod(from, to)
    }

    /** All expenses, no date filter. */
    fun all(): Flow<List<Expense>> = repository.observeExpenses()
}

internal fun YearMonth.toEpochMillisRange(zone: ZoneId = ZoneId.systemDefault()): Pair<Long, Long> {
    val start = atDay(1).atStartOfDay(zone).toInstant().toEpochMilli()
    val end = plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1
    return start to end
}
