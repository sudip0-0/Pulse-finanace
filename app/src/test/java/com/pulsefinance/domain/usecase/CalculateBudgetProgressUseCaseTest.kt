package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.BudgetStatus
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculateBudgetProgressUseCaseTest {
    private val useCase = CalculateBudgetProgressUseCase()

    @Test
    fun returnsUnderWhenBelowSeventyPercent() {
        val result = useCase(Money(5000), Money(10000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.Under, value.status)
        assertEquals(0.5f, value.percent, 0.001f)
        assertEquals(Money(5000), value.remaining)
    }

    @Test
    fun returnsWarningAtSeventyPercent() {
        val result = useCase(Money(7000), Money(10000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.Warning, value.status)
        assertEquals(Money(3000), value.remaining)
    }

    @Test
    fun returnsDangerAboveNinetyPercent() {
        val result = useCase(Money(9500), Money(10000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.Danger, value.status)
        assertEquals(0.95f, value.percent, 0.001f)
    }

    @Test
    fun returnsOverBudgetWhenSpentExceedsBudget() {
        val result = useCase(Money(12000), Money(10000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.OverBudget, value.status)
        assertEquals(Money(-2000), value.remaining)
    }

    @Test
    fun zeroSpendReturnsUnder() {
        val result = useCase(Money(0), Money(350000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.Under, value.status)
        assertEquals(0f, value.percent, 0.001f)
        assertEquals(Money(350000), value.remaining)
    }

    @Test
    fun rejectsZeroBudget() {
        val result = useCase(Money(100), Money(0))
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun rejectsCurrencyMismatch() {
        val result = useCase(Money(100, "NPR"), Money(100, "USD"))
        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun typicalNepalBudgetScenario() {
        // Monthly budget: रू 35,000 (3500000 paisa)
        // Spent: रू 24,185 (2418500 paisa)
        val result = useCase(Money(2418500), Money(3500000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.Under, value.status)
        assertEquals(Money(1081500), value.remaining)
        assertEquals(0.691f, value.percent, 0.001f)
    }
}
