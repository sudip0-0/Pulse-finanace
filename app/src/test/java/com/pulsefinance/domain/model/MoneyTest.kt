package com.pulsefinance.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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

    @Test
    fun zeroFormatsCorrectly() {
        assertEquals("रू 0.00", Money.zero().format())
    }

    @Test
    fun subtractsCorrectly() {
        assertEquals(Money(50), Money(150) - Money(100))
    }

    @Test
    fun isPositiveReturnsTrueForPositiveAmount() {
        assertTrue(Money(1).isPositive())
    }

    @Test
    fun isPositiveReturnsFalseForZero() {
        assertFalse(Money(0).isPositive())
    }

    @Test
    fun isPositiveReturnsFalseForNegative() {
        assertFalse(Money(-100).isPositive())
    }

    @Test
    fun comparesToOtherMoney() {
        assertTrue(Money(200) > Money(100))
        assertTrue(Money(100) < Money(200))
        assertEquals(0, Money(100).compareTo(Money(100)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsCurrencyMismatchOnAdd() {
        Money(100, "NPR") + Money(100, "USD")
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsCurrencyMismatchOnCompare() {
        Money(100, "NPR").compareTo(Money(100, "USD"))
    }

    @Test
    fun formatsSmallAmountsWithLeadingZeroPaisa() {
        assertEquals("रू 0.05", Money(5).format())
        assertEquals("रू 0.50", Money(50).format())
    }

    @Test
    fun formatsTypicalNepalExpenses() {
        // Pathao ride: रू 280
        assertEquals("रू 280.00", Money(28000).format())
        // WorldLink internet: रू 1,500
        assertEquals("रू 1,500.00", Money(150000).format())
        // Room rent: रू 18,000
        assertEquals("रू 18,000.00", Money(1800000).format())
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsBlankCurrencyCode() {
        Money(100, "")
    }
}
