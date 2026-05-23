package com.pulsefinance.data.local.database

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class PulseTypeConvertersTest {
    private val converters = PulseTypeConverters()

    @Test
    fun localDateRoundTripUsesIsoDate() {
        val value = LocalDate.of(2026, 5, 23)

        val stored = converters.localDateToString(value)

        assertEquals("2026-05-23", stored)
        assertEquals(value, converters.stringToLocalDate(stored))
    }

    @Test
    fun instantRoundTripUsesIsoInstant() {
        val value = Instant.parse("2026-05-23T10:15:30Z")

        val stored = converters.instantToString(value)

        assertEquals("2026-05-23T10:15:30Z", stored)
        assertEquals(value, converters.stringToInstant(stored))
    }

    @Test
    fun yearMonthRoundTripUsesIsoMonth() {
        val value = YearMonth.of(2026, 5)

        val stored = converters.yearMonthToString(value)

        assertEquals("2026-05", stored)
        assertEquals(value, converters.stringToYearMonth(stored))
    }
}
