package com.pulsefinance.presentation.analytics

import com.pulsefinance.domain.FakeBudgetRepository
import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.usecase.CalculateBudgetProgressUseCase
import com.pulsefinance.domain.usecase.ObserveDashboardUseCase
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val categories = listOf(
        category(1, "Food & Dining"),
        category(3, "Transport"),
    )
    private val today = LocalDate.now()
    private val expenses = listOf(
        expense(id = 1, title = "Pathao ride", categoryId = 3, amountMinor = 28000, date = today),
        expense(id = 2, title = "Lunch momo", categoryId = 1, amountMinor = 50000, date = today),
        expense(id = 3, title = "Daraz order", categoryId = 3, amountMinor = 125000, date = today),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        expenseRepository: FakeExpenseRepository = FakeExpenseRepository(expenses),
    ): AnalyticsViewModel {
        val categoryRepository = FakeCategoryRepository(categories)
        val budgetRepository = FakeBudgetRepository(null)
        val observeDashboard = ObserveDashboardUseCase(
            expenseRepository = expenseRepository,
            categoryRepository = categoryRepository,
            budgetRepository = budgetRepository,
            calculateBudgetProgress = CalculateBudgetProgressUseCase(),
        )
        return AnalyticsViewModel(observeDashboard)
    }

    @Test
    fun `loads category breakdown on init`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(2, state.categoryBreakdown.size)
    }

    @Test
    fun `percentages are calculated correctly`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        // Total = 28000 + 50000 + 125000 = 203000
        // Transport = 153000 -> 75%
        // Food = 50000 -> 24%
        val transport = state.categoryBreakdown.first { it.name == "Transport" }
        val food = state.categoryBreakdown.first { it.name == "Food & Dining" }
        assertEquals(75, transport.percent)
        assertEquals(24, food.percent)
    }

    @Test
    fun `sweep angles sum to less than 360 accounting for gaps`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val totalSweep = vm.uiState.value.categoryBreakdown.sumOf { it.sweepAngle.toDouble() }
        // With 2 categories and 4-degree gaps, available = 360 - 8 = 352
        assertTrue(totalSweep <= 352.1) // small float tolerance
        assertTrue(totalSweep > 350.0)
    }

    @Test
    fun `total spend is formatted correctly`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        // Total = 203000 paisa = 2030.00
        assertEquals("रू 2,030.00", vm.uiState.value.totalSpend)
    }

    @Test
    fun `accessibility summary includes all categories`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val summary = vm.uiState.value.chartAccessibilitySummary
        assertTrue(summary.contains("Total spend"))
        assertTrue(summary.contains("Transport"))
        assertTrue(summary.contains("Food & Dining"))
    }

    @Test
    fun `empty state when no expenses`() = runTest {
        val vm = createViewModel(FakeExpenseRepository())
        advanceUntilIdle()

        assertTrue(vm.uiState.value.categoryBreakdown.isEmpty())
        assertEquals("रू 0.00", vm.uiState.value.totalSpend)
    }

    @Test
    fun `period change updates data`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onPeriodSelected(AnalyticsPeriod.LastMonth)
        advanceUntilIdle()

        // Last month has no expenses in our test data (all are today)
        assertEquals("Last month", vm.uiState.value.periodLabel)
        assertTrue(vm.uiState.value.categoryBreakdown.isEmpty())
    }
}
