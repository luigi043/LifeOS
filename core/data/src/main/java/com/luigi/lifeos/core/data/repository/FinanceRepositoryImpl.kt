package com.luigi.lifeos.core.data.repository

import com.luigi.lifeos.core.database.dao.BudgetDao
import com.luigi.lifeos.core.database.dao.ExpenseCategoryDao
import com.luigi.lifeos.core.database.dao.ExpenseDao
import com.luigi.lifeos.core.database.dao.IncomeDao
import com.luigi.lifeos.core.database.entity.*          // fixes ambiguous toDomain/toEntity imports
import com.luigi.lifeos.core.domain.model.Budget
import com.luigi.lifeos.core.domain.model.DefaultExpenseCategories
import com.luigi.lifeos.core.domain.model.Expense
import com.luigi.lifeos.core.domain.model.ExpenseCategory
import com.luigi.lifeos.core.domain.model.Income
import com.luigi.lifeos.core.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinanceRepositoryImpl @Inject constructor(
    private val expenseCategoryDao: ExpenseCategoryDao,
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val budgetDao: BudgetDao,
) : FinanceRepository {

    // ── Expenses ────────────────────────────────────────────────────────────

    override fun observeExpenses(): Flow<List<Expense>> =
        expenseDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeExpensesByPeriod(
        fromMillis: Long,
        toMillis: Long,
    ): Flow<List<Expense>> =
        expenseDao.observeByPeriod(fromMillis, toMillis)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun upsertExpense(expense: Expense): Long =
        expenseDao.upsert(expense.toEntity())

    override suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense.toEntity())
    }

    // ── Income ──────────────────────────────────────────────────────────────

    override fun observeIncome(): Flow<List<Income>> =
        incomeDao.observeAll().map { list -> list.map { it.toDomain() } }

    // Fix #4: expose period-filtered income so callers avoid full table scans
    override fun observeIncomeByPeriod(fromMillis: Long, toMillis: Long): Flow<List<Income>> =
        incomeDao.observeByPeriod(fromMillis, toMillis)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun upsertIncome(income: Income): Long =
        incomeDao.upsert(income.toEntity())

    override suspend fun deleteIncome(income: Income) {
        incomeDao.delete(income.toEntity())
    }

    // ── Budgets ─────────────────────────────────────────────────────────────

    override fun observeBudgetForPeriod(fromMillis: Long, toMillis: Long): Flow<Budget?> =
        budgetDao.observeOverallForPeriod(fromMillis, toMillis)
            .map { it?.toDomain() }

    override suspend fun upsertBudget(budget: Budget): Long =
        budgetDao.upsert(budget.toEntity())

    // ── Categories ──────────────────────────────────────────────────────────

    override fun observeCategories(): Flow<List<ExpenseCategory>> =
        expenseCategoryDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun upsertCategory(category: ExpenseCategory): Long =
        expenseCategoryDao.upsert(category.toEntity())

    override suspend fun seedDefaultCategoriesIfNeeded() {
        if (expenseCategoryDao.count() == 0) {
            expenseCategoryDao.upsertAll(
                DefaultExpenseCategories.all.map { it.toEntity() }
            )
        }
    }

    // ── Aggregates ──────────────────────────────────────────────────────────

    override suspend fun totalSpentInPeriod(fromMillis: Long, toMillis: Long): Long =
        expenseDao.sumByPeriod(fromMillis, toMillis) ?: 0L
}
