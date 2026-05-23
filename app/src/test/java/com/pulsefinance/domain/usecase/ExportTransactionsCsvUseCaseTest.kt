package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.DomainResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportTransactionsCsvUseCaseTest {

    private val categories = listOf(
        category(3, "Transport"),
        category(1, "Food & Dining"),
        category(9, "Internet & TV"),
    )
    private val useCase = ExportTransactionsCsvUseCase(FakeCategoryRepository(categories))

    @Test
    fun escapesCommasQuotesAndNewLines() = runBlocking {
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

    @Test
    fun headerRowContainsAllColumns() = runBlocking {
        val result = useCase(emptyList())

        val csv = (result as DomainResult.Success).value
        val header = csv.lines().first()
        assertEquals("Date,Title,Merchant,Category,Amount,Currency,Payment Method,Note,Recurring", header)
    }

    @Test
    fun preservesNepaliUnicodeText() = runBlocking {
        val result = useCase(
            listOf(
                expense(
                    title = "भात भाटेनी खरिद",
                    merchant = "भात-भाटेनी",
                    note = "तरकारी र फलफूल",
                ),
            ),
        )

        val csv = (result as DomainResult.Success).value
        assertTrue(csv.contains("भात भाटेनी खरिद"))
        assertTrue(csv.contains("भात-भाटेनी"))
        assertTrue(csv.contains("तरकारी र फलफूल"))
    }

    @Test
    fun includesRecurringColumnForGeneratedExpenses() = runBlocking {
        val result = useCase(
            listOf(
                expense(title = "WorldLink internet", recurringRuleId = 1),
            ),
        )

        val csv = (result as DomainResult.Success).value
        val dataRow = csv.lines()[1]
        assertTrue(dataRow.endsWith("true"))
    }

    @Test
    fun nonRecurringExpenseShowsFalse() = runBlocking {
        val result = useCase(
            listOf(
                expense(title = "Pathao ride"),
            ),
        )

        val csv = (result as DomainResult.Success).value
        val dataRow = csv.lines()[1]
        assertTrue(dataRow.endsWith("false"))
    }

    @Test
    fun amountFormattedWithNprSymbol() = runBlocking {
        val result = useCase(
            listOf(
                expense(title = "Lunch", amountMinor = 45050),
            ),
        )

        val csv = (result as DomainResult.Success).value
        assertTrue(csv.contains("रू 450.50"))
        assertTrue(csv.contains("NPR"))
    }

    @Test
    fun emptyMerchantAndNoteProduceEmptyFields() = runBlocking {
        val result = useCase(
            listOf(
                expense(title = "Misc", merchant = null, note = null),
            ),
        )

        val csv = (result as DomainResult.Success).value
        val dataRow = csv.lines()[1]
        val fields = parseCsvRow(dataRow)
        assertEquals("", fields[2]) // merchant
        assertEquals("", fields[7]) // note
    }

    @Test
    fun multipleExpensesProduceMultipleRows() = runBlocking {
        val result = useCase(
            listOf(
                expense(title = "Pathao ride"),
                expense(title = "Foodmandu order", categoryId = 1),
                expense(title = "WorldLink", categoryId = 9),
            ),
        )

        val csv = (result as DomainResult.Success).value
        val lines = csv.lines()
        assertEquals(4, lines.size) // header + 3 data rows
    }

    // Simple CSV parser for testing (handles quoted fields)
    private fun parseCsvRow(row: String): List<String> {
        val fields = mutableListOf<String>()
        var i = 0
        while (i <= row.length) {
            if (i == row.length) {
                fields.add("")
                break
            }
            if (row[i] == '"') {
                val sb = StringBuilder()
                i++ // skip opening quote
                while (i < row.length) {
                    if (row[i] == '"') {
                        if (i + 1 < row.length && row[i + 1] == '"') {
                            sb.append('"')
                            i += 2
                        } else {
                            i++ // skip closing quote
                            break
                        }
                    } else {
                        sb.append(row[i])
                        i++
                    }
                }
                fields.add(sb.toString())
                if (i < row.length && row[i] == ',') i++ // skip comma
            } else {
                val commaIdx = row.indexOf(',', i)
                if (commaIdx == -1) {
                    fields.add(row.substring(i))
                    break
                } else {
                    fields.add(row.substring(i, commaIdx))
                    i = commaIdx + 1
                }
            }
        }
        return fields
    }
}
