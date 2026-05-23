package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Money
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AddExpenseUseCaseTest {
    private val clock = Clock.fixed(Instant.parse("2026-05-23T10:15:30Z"), ZoneOffset.UTC)

    @Test
    fun rejectsMissingCategory() = runBlocking {
        val useCase = AddExpenseUseCase(
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(emptyList()),
            clock = clock,
        )

        val result = useCase(expense())

        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun savesValidExpense() = runBlocking {
        val useCase = AddExpenseUseCase(
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(listOf(category(3, "Transport"))),
            clock = clock,
        )

        val result = useCase(expense(amountMinor = 5000))

        assertTrue(result is DomainResult.Success)
    }

    @Test(expected = IllegalArgumentException::class)
    fun expenseModelRejectsInvalidAmount() {
        expense(amountMinor = 0).copy(amount = Money(0))
    }

    @Test
    fun rejectsFutureExpenseDate() = runBlocking {
        val useCase = AddExpenseUseCase(
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(listOf(category(3, "Transport"))),
            clock = clock,
        )

        val result = useCase(expense(date = LocalDate.of(2026, 5, 24)))

        assertTrue(result is DomainResult.Failure)
    }
}
