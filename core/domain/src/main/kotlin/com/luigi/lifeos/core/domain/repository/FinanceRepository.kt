package com.luigi.lifeos.core.domain.repository

import com.luigi.lifeos.core.domain.model.Budget
import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.model.Income
import kotlinx.coroutines.flow.Flow

/**
 * Finance data contract — interface lives in :core:domain so feature modules
 * and cross-system use cases (e.g. StabilityScore budget leg) inject only this,
 * never Room types. The Room-backed implementation is in :core:data.
 */
interface FinanceRepository {

    // ── Expenses ────────────────────────────────────────────────────────────

    /** All expenses, newest first. */
    fun observeExpenses(): Flow<List<Expense>>

    /** Expenses whose [Expense.dateEpochMillis] falls within the given window. */
    fun observeExpensesByPeriod(fromMillis: Long, toMillis: Long): Flow<List<Expense>>

    /** Insert or replace an expense. Returns the row id. */
    suspend fun upsertExpense(expense: Expense): Long

    suspend fun deleteExpense(expense: Expense)

    // ── Income ──────────────────────────────────────────────────────────────

    /** All income entries, newest first. */
    fun observeIncome(): Flow<List<Income>>

    /** Income whose [Income.dateEpochMillis] falls within the given window. */
    fun observeIncomeByPeriod(fromMillis: Long, toMillis: Long): Flow<List<Income>>

    suspend fun upsertIncome(income: Income): Long

    suspend fun deleteIncome(income: Income)

    // ── Budgets ─────────────────────────────────────────────────────────────

    /**
     * The overall budget (categoryId = null) for the period overlapping
     * [fromMillis]..[toMillis]. Returns null if none has been set.
     */
    fun observeBudgetForPeriod(fromMillis: Long, toMillis: Long): Flow<Budget?>

    suspend fun upsertBudget(budget: Budget): Long

    // ── Categories ──────────────────────────────────────────────────────────

    fun observeCategories(): Flow<List<ExpenseCategory>>

    suspend fun upsertCategory(category: ExpenseCategory): Long

    /**
     * Seeds [DefaultExpenseCategories] if the table is empty.
     * Idempotent — safe to call on every cold start.
     */
    suspend fun seedDefaultCategoriesIfNeeded()

    // ── Aggregates ──────────────────────────────────────────────────────────

    /** Sum of all expense amounts in the period, in minor units. 0 if none. */
    suspend fun totalSpentInPeriod(fromMillis: Long, toMillis: Long): Long
}
