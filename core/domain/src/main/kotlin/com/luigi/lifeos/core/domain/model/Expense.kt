package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable

/**
 * A single expense entry. Amount is stored as minor units (cents) matching [Money].
 *
 * [categoryId] references [ExpenseCategory.id]. No hard FK in the domain —
 * Room enforces integrity at the DB layer.
 *
 * [dateEpochMillis] is the user-facing transaction date (can differ from creation).
 * [createdAtEpochMillis] is the insert timestamp for audit/ordering.
 */
@Serializable
data class Expense(
    val id: Long = 0,
    val amountMinor: Long,
    val currency: String = "EUR",
    val categoryId: Long,
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod? = null,
    val dateEpochMillis: Long,
    val createdAtEpochMillis: Long,
)

/** Convenience — wraps [Expense.amountMinor] in a typed [Money] value. */
fun Expense.money(): Money = Money(amountMinor)
