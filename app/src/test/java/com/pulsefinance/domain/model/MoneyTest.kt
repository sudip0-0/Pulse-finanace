package com.pulsefinance.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MoneyTest {
    @Test
    fun formatsNprMinorUnits() {
        assertEquals("रू 2,418.50", Money(241850).format())
    }

    @Test
    fun formatsNegativeMinorUnitsWithoutFloatingPointMath() {
        assertEquals("-रू 2,418.05", Money(-241805).format())
    }

    @Test
    fun formatsLargeMinorUnitsPredictably() {
        assertEquals("रू 92,233,720,368,547,758.07", Money(Long.MAX_VALUE).format())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsLongMinValueFormatting() {
        Money(Long.MIN_VALUE).format()
    }

    @Test
    fun addsOnlyMatchingCurrencies() {
        assertEquals(Money(300), Money(100) + Money(200))
    }
}
