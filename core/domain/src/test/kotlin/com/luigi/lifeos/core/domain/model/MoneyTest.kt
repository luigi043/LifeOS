package com.luigi.lifeos.core.domain.model

import org.junit.Assert.*
import org.junit.Test

class MoneyTest {
    @Test fun `decimal input preserves every cent`() {
        assertEquals(29L, Money.parseMinorUnits("0.29"))
        assertEquals(1001L, Money.parseMinorUnits("10,01"))
        assertEquals(120050L, Money.parseMinorUnits(" 1200.50 "))
        assertEquals(0L, Money.parseMinorUnits("0"))
    }

    @Test fun `ambiguous invalid and overflowing amounts are rejected`() {
        listOf("", "NaN", "Infinity", "-1", "1e2", "1.001", "1,000.00", "1..2", "9999999999999999999999")
            .forEach { assertNull(it, Money.parseMinorUnits(it)) }
    }

    @Test fun `major unit conversion rounds instead of truncating`() {
        assertEquals(29L, Money.fromMajor(0.29).minorUnits)
        assertEquals(101L, Money.fromMajor(1.005).minorUnits)
        assertEquals(-101L, Money.fromMajor(-1.005).minorUnits)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `nonfinite major amount is rejected`() { Money.fromMajor(Double.NaN) }
}
