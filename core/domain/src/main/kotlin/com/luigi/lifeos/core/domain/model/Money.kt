package com.luigi.lifeos.core.domain.model

import kotlinx.serialization.Serializable

/**
 * Money is stored as integer minor-units (cents) to avoid floating-point error.
 * Currency is a free-form ISO 4217 code rather than an enum to support the
 * forward roadmap (multi-currency, conversion via :feature:finance converter).
 */
@JvmInline
@Serializable
value class Money(val minorUnits: Long) {
    operator fun plus(other: Money) = Money(minorUnits + other.minorUnits)
    operator fun minus(other: Money) = Money(minorUnits - other.minorUnits)
    operator fun compareTo(other: Money) = minorUnits.compareTo(other.minorUnits)

    fun toMajorUnits(): Double = minorUnits / 100.0

    companion object {
        val Zero = Money(0)
        fun fromMajor(amount: Double) = Money((amount * 100).toLong())
    }
}

@Serializable
data class CurrencyAmount(val amount: Money, val currency: String)
