package com.luigi.lifeos.core.domain.model

/**
 * Computed view of a period's budget health — produced by [GetBudgetSummaryUseCase].
 *
 * All amounts are in minor units (cents) matching [Money].
 * [usedFraction] is clamped to 0..1 so a progress bar never overflows; check
 * [isOverBudget] separately to show a warning state.
 */
data class BudgetSummary(
    val totalBudgetMinor: Long,
    val totalSpentMinor: Long,
    val currency: String,
    val categoryBreakdown: List<CategorySpend>,
) {
    val remainingMinor: Long
        get() = totalBudgetMinor - totalSpentMinor

    val usedFraction: Float
        get() = if (totalBudgetMinor == 0L) 0f
                else (totalSpentMinor.toFloat() / totalBudgetMinor).coerceIn(0f, 1f)

    val isOverBudget: Boolean
        get() = totalSpentMinor > totalBudgetMinor

    /** Per-category breakdown used in the analytics charts. */
    data class CategorySpend(
        val category: ExpenseCategory,
        val spentMinor: Long,
        val limitMinor: Long? = null,   // null = no per-category budget set
    ) {
        val fraction: Float
            get() = if (limitMinor == null || limitMinor == 0L) 0f
                    else (spentMinor.toFloat() / limitMinor).coerceIn(0f, 1f)
    }
}
