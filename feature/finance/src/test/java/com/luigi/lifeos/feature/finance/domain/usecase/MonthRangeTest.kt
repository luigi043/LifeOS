package com.luigi.lifeos.feature.finance.domain.usecase
import java.time.YearMonth
import java.time.ZoneId
import org.junit.Assert.*
import org.junit.Test
class MonthRangeTest {
    @Test fun includesFinalMillisecondAndUsesLocalCalendarAcrossDst() {
        val zone = ZoneId.of("Europe/Lisbon")
        val month = YearMonth.of(2026, 10)
        val (start, end) = month.toEpochMillisRange(zone)
        assertEquals(month.atDay(1).atStartOfDay(zone).toInstant().toEpochMilli(), start)
        assertEquals(month.plusMonths(1).atDay(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1, end)
    }
}
