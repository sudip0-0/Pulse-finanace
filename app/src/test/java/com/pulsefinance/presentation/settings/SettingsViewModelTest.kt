package com.pulsefinance.presentation.settings

import com.pulsefinance.domain.FakeBudgetRepository
import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.Budget
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.usecase.ExportTransactionsCsvUseCase
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadsBudgetOnInit() = runTest {
        val budget = Budget(
            id = 1,
            month = YearMonth.now(),
            amount = Money(350000),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )
        val viewModel = createViewModel(budget = budget)
        advanceUntilIdle()

        assertEquals("रू 3,500.00", viewModel.uiState.value.budgetDisplayLabel)
        assertEquals("3500", viewModel.uiState.value.budgetAmountText)
    }

    @Test
    fun showsNotSetWhenNoBudget() = runTest {
        val viewModel = createViewModel(budget = null)
        advanceUntilIdle()

        assertEquals("Not set", viewModel.uiState.value.budgetDisplayLabel)
    }

    @Test
    fun saveBudgetValidatesEmptyAmount() = runTest {
        val viewModel = createViewModel(budget = null)
        advanceUntilIdle()

        viewModel.onSaveBudget()
        advanceUntilIdle()

        assertEquals("Enter a valid budget amount.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun saveBudgetUpdatesDisplayLabel() = runTest {
        val viewModel = createViewModel(budget = null)
        advanceUntilIdle()

        viewModel.onBudgetAmountChanged("5000")
        viewModel.onSaveBudget()
        advanceUntilIdle()

        assertEquals("रू 5,000.00", viewModel.uiState.value.budgetDisplayLabel)
        assertTrue(viewModel.uiState.value.budgetSaved)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun exportCsvProducesReadyState() = runTest {
        val today = LocalDate.now()
        val expenses = listOf(
            expense(title = "Pathao ride", date = today),
        )
        val viewModel = createViewModel(budget = null, expenses = expenses)
        advanceUntilIdle()

        viewModel.onExportCsv()
        advanceUntilIdle()

        val exportState = viewModel.uiState.value.exportState
        assertTrue(exportState is ExportState.Ready)
        val csv = (exportState as ExportState.Ready).csvContent
        assertTrue(csv.contains("Pathao ride"))
        assertTrue(csv.contains("Date,Title,Merchant"))
    }

    @Test
    fun exportCompleteResetsState() = runTest {
        val today = LocalDate.now()
        val expenses = listOf(expense(title = "Test", date = today))
        val viewModel = createViewModel(budget = null, expenses = expenses)
        advanceUntilIdle()

        viewModel.onExportCsv()
        advanceUntilIdle()
        viewModel.onExportComplete()

        assertEquals(ExportState.Idle, viewModel.uiState.value.exportState)
    }

    @Test
    fun currencyLabelShowsNpr() = runTest {
        val viewModel = createViewModel(budget = null)
        advanceUntilIdle()

        assertEquals("NPR (रू)", viewModel.uiState.value.currencyLabel)
    }

    private fun createViewModel(
        budget: Budget?,
        expenses: List<com.pulsefinance.domain.model.Expense> = emptyList(),
    ): SettingsViewModel {
        val categories = listOf(category(3, "Transport"))
        return SettingsViewModel(
            budgetRepository = FakeBudgetRepository(budget),
            expenseRepository = FakeExpenseRepository(expenses),
            exportCsvUseCase = ExportTransactionsCsvUseCase(FakeCategoryRepository(categories)),
        )
    }
}
