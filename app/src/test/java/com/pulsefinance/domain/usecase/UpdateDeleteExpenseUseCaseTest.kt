package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.DomainResult
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateDeleteExpenseUseCaseTest {
    private val clock = Clock.fixed(Instant.parse("2026-05-23T10:15:30Z"), ZoneOffset.UTC)

    @Test
    fun updateRejectsMissingId() = runBlocking {
        val useCase = UpdateExpenseUseCase(
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(listOf(category(3, "Transport"))),
            clock = clock,
        )

        val result = useCase(expense(id = 0))

        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun updateRejectsMissingExpense() = runBlocking {
        val useCase = UpdateExpenseUseCase(
            expenseRepository = FakeExpenseRepository(),
            categoryRepository = FakeCategoryRepository(listOf(category(3, "Transport"))),
            clock = clock,
        )

        val result = useCase(expense(id = 99))

        assertTrue(result is DomainResult.Failure)
    }

    @Test
    fun deleteRejectsMissingExpense() = runBlocking {
        val useCase = DeleteExpenseUseCase(FakeExpenseRepository())

        val result = useCase(99)

        assertTrue(result is DomainResult.Failure)
    }
}
