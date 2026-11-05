package com.luigi.lifeos.feature.finance.domain.usecase

import com.luigi.lifeos.core.domain.model.BudgetSummary
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject
import com.luigi.lifeos.core.datastore.UserPreferences
import com.luigi.lifeos.core.domain.repository.UserProfileRepository

/**
 * Combines the active [Budget] with this month's expenses to produce a
 * [BudgetSummary] — the headline number shown in the Finance dashboard card.
 *
 * If no budget has been set, returns a zero-limit summary so the UI can
 * prompt the user to configure one.
 */
class GetBudgetSummaryUseCase @Inject constructor(
    private val repository: FinanceRepository,
    private val preferences: UserPreferences,
    private val profiles: UserProfileRepository,
) {
    operator fun invoke(yearMonth: YearMonth = YearMonth.now()): Flow<BudgetSummary> {
        val (from, to) = yearMonth.toEpochMillisRange()

        return combine(
            repository.observeBudgetForPeriod(from, to),
            repository.observeExpensesByPeriod(from, to),
            repository.observeCategories(),
            preferences.baseCurrency,
            profiles.observe(),
        ) { budget, allExpenses, categories, currency, profile ->
            val expenses = allExpenses.filter { it.currency == currency }

            val totalSpent = expenses.sumOf { it.amountMinor }
            val limit = budget?.takeIf { it.currency == currency }?.limitMinor
                ?: profile?.takeIf { it.preferredCurrency == currency }?.monthlyBudgetMinor ?: 0L

            val categoryMap: Map<Long, ExpenseCategory> = categories.associateBy { it.id }

            val breakdown = expenses
                .groupBy { it.categoryId }
                .map { (catId, catExpenses) ->
                    BudgetSummary.CategorySpend(
                        category     = categoryMap[catId]
                            ?: ExpenseCategory(catId, "Unknown"),
                        spentMinor   = catExpenses.sumOf { it.amountMinor },
                        limitMinor   = null,
                    )
                }
                .sortedByDescending { it.spentMinor }

            BudgetSummary(
                totalBudgetMinor   = limit,
                totalSpentMinor    = totalSpent,
                currency           = currency,
                categoryBreakdown  = breakdown,
            )
        }
    }
}
