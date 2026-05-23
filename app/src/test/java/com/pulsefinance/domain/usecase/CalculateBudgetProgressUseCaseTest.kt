package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.BudgetStatus
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Money
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculateBudgetProgressUseCaseTest {
    private val useCase = CalculateBudgetProgressUseCase()

    @Test
    fun returnsWarningAtSeventyPercent() {
        val result = useCase(Money(7000), Money(10000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.Warning, value.status)
        assertEquals(Money(3000), value.remaining)
    }

    @Test
    fun returnsOverBudgetWhenSpentExceedsBudget() {
        val result = useCase(Money(12000), Money(10000))

        val value = (result as DomainResult.Success).value
        assertEquals(BudgetStatus.OverBudget, value.status)
        assertEquals(Money(-2000), value.remaining)
    }
}
