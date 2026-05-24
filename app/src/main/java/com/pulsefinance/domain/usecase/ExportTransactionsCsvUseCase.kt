package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.repository.CategoryRepository

class ExportTransactionsCsvUseCase(
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(expenses: List<Expense>): DomainResult<String> {
        return try {
            val header = listOf("Date", "Title", "Merchant", "Category", "Amount", "Currency", "Payment Method", "Note", "Recurring")
            val rows = expenses.map { expense ->
                val category = categoryRepository.getCategory(expense.categoryId)
                listOf(
                    expense.expenseDate.toString(),
                    expense.title,
                    expense.merchant.orEmpty(),
                    category?.name.orEmpty(),
                    formatAmountForCsv(expense.amount.amountMinor),
                    expense.amount.currencyCode,
                    expense.paymentMethod?.name.orEmpty(),
                    expense.note.orEmpty(),
                    expense.isRecurringGenerated.toString(),
                )
            }
            DomainResult.Success((listOf(header) + rows).joinToString("\r\n") { row -> row.joinToString(",") { it.csvEscaped() } })
        } catch (error: Throwable) {
            DomainResult.Failure(DomainError.Repository("Could not export transactions.", error))
        }
    }

    private fun formatAmountForCsv(amountMinor: Long): String {
        val sign = if (amountMinor < 0) "-" else ""
        val absMinor = if (amountMinor < 0) -amountMinor else amountMinor
        val major = absMinor / 100
        val minor = absMinor % 100
        return "$sign$major.${minor.toString().padStart(2, '0')}"
    }

    private fun String.csvEscaped(): String {
        val escaped = replace("\"", "\"\"")
        return if (any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
            "\"$escaped\""
        } else {
            escaped
        }
    }
}
