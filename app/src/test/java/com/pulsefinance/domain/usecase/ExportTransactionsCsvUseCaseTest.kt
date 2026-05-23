package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.DomainResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportTransactionsCsvUseCaseTest {
    @Test
    fun escapesCommasQuotesAndNewLines() = runBlocking {
        val useCase = ExportTransactionsCsvUseCase(FakeCategoryRepository(listOf(category(3, "Transport"))))

        val result = useCase(
            listOf(
                expense(
                    title = "Pathao, ride",
                    merchant = "Pathao \"Bike\"",
                    note = "Baneshwor\nKathmandu",
                ),
            ),
        )

        val csv = (result as DomainResult.Success).value
        assertTrue(csv.contains("\"Pathao, ride\""))
        assertTrue(csv.contains("\"Pathao \"\"Bike\"\"\""))
        assertTrue(csv.contains("\"Baneshwor\nKathmandu\""))
    }
}
