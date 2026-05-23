package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Money
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AddExpenseUseCaseTest {
    @Test
    fun rejectsMissingCategory() = runBlocking {
        val useCase = AddExpenseUseCase(
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(emptyList()),
        )

        val result = useCase(expense())

        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun savesValidExpense() = runBlocking {
        val useCase = AddExpenseUseCase(
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(listOf(category(3, "Transport"))),
        )

        val result = useCase(expense(amountMinor = 5000))

        assertTrue(result is DomainResult.Success)
    }

    @Test(expected = IllegalArgumentException::class)
    fun expenseModelRejectsInvalidAmount() {
        expense(amountMinor = 0).copy(amount = Money(0))
    }
}
