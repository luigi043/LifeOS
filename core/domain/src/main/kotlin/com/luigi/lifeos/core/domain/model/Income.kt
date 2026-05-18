package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable

/**
 * An income entry — salary, freelance, side-income, etc.
 *
 * [source] is a free-text label ("Salary", "Freelance client X").
 * Recurring income (e.g. monthly salary) sets [isRecurring] + [recurringPeriod]
 * so the finance summary can project future cash flow.
 */
@Serializable
data class Income(
    val id: Long = 0,
    val amountMinor: Long,
    val currency: String = "EUR",
    val source: String,
    val isRecurring: Boolean = false,
    val recurringPeriod: RecurringPeriod? = null,
    val dateEpochMillis: Long,
    val createdAtEpochMillis: Long,
)

fun Income.money(): Money = Money(amountMinor)
