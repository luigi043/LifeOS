package com.luigi.lifeos.feature.finance.domain.usecase

import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class MonthlyAnalytics(
    val yearMonth: YearMonth,
    val totalSpentMinor: Long,
    val totalIncomeMinor: Long,
    val expensesByCategory: List<CategoryTotal>,
    val dailyTotals: Map<Int, Long>,          // day-of-month → minor units
)

data class CategoryTotal(
    val category: ExpenseCategory,
    val amountMinor: Long,
    val percentage: Float,
)

class GetMonthlyAnalyticsUseCase @Inject constructor(
    private val repository: FinanceRepository,
) {
    operator fun invoke(yearMonth: YearMonth = YearMonth.now()): Flow<MonthlyAnalytics> {
        val (from, to) = yearMonth.toEpochMillisRange()

        return combine(
            repository.observeExpensesByPeriod(from, to),
            repository.observeIncomeByPeriod(from, to),   // Fix: period-filtered, not full table
            repository.observeCategories(),
        ) { expenses, incomeList, categories ->

            val totalSpent  = expenses.sumOf { it.amountMinor }
            val totalIncome = incomeList.sumOf { it.amountMinor }

            val categoryMap = categories.associateBy { it.id }

            val byCategory = expenses
                .groupBy { it.categoryId }
                .map { (catId, catExpenses) ->
                    val spent = catExpenses.sumOf { it.amountMinor }
                    CategoryTotal(
                        category    = categoryMap[catId] ?: ExpenseCategory(catId, "Unknown"),
                        amountMinor = spent,
                        percentage  = if (totalSpent == 0L) 0f
                                      else spent.toFloat() / totalSpent,
                    )
                }
                .sortedByDescending { it.amountMinor }

            val dailyTotals = expenses
                .groupBy { dayOfMonth(it) }     // Fix: timezone-aware grouping
                .mapValues { (_, dayExpenses) -> dayExpenses.sumOf { it.amountMinor } }

            MonthlyAnalytics(
                yearMonth          = yearMonth,
                totalSpentMinor    = totalSpent,
                totalIncomeMinor   = totalIncome,
                expensesByCategory = byCategory,
                dailyTotals        = dailyTotals,
            )
        }
    }

    /**
     * Returns the day-of-month (1-based) for an expense using the device timezone,
     * so expenses logged near midnight are bucketed to the day the user perceives.
     */
    private fun dayOfMonth(expense: Expense): Int =
        Instant.ofEpochMilli(expense.dateEpochMillis)
            .atZone(ZoneId.systemDefault())
            .dayOfMonth
}
