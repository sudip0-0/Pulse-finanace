package com.pulsefinance.presentation.dashboard

import com.pulsefinance.domain.FakeBudgetRepository
import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.FakeRecurringRuleRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.Budget
import com.pulsefinance.domain.model.BudgetStatus
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.usecase.CalculateBudgetProgressUseCase
import com.pulsefinance.domain.usecase.GenerateDueRecurringExpensesUseCase
import com.pulsefinance.domain.usecase.ObserveDashboardUseCase
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val today = LocalDate.now()
    private val currentMonth = YearMonth.now()

    private val categories = listOf(
        category(1, "Food & Dining"),
        category(3, "Transport"),
        category(9, "Internet & TV"),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun showsLoadingInitially() = runTest {
        val viewModel = createViewModel()
        // Before advancing, state should be loading
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun loadsExpensesAndShowsMonthlySpend() = runTest {
        val expenses = listOf(
            expense(title = "Pathao ride", amountMinor = 28000, date = today),
            expense(title = "Lunch", amountMinor = 45000, categoryId = 1, date = today),
        )
        val viewModel = createViewModel(expenses = expenses)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("रू 730.00", state.monthlySpend)
    }

    @Test
    fun showsEmptyStateWhenNoExpenses() = runTest {
        val viewModel = createViewModel(expenses = emptyList())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isEmpty)
        assertEquals("रू 0.00", state.monthlySpend)
    }

    @Test
    fun showsBudgetProgressWhenBudgetExists() = runTest {
        val budget = Budget(
            id = 1,
            month = currentMonth,
            amount = Money(350000),
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
        )
        val expenses = listOf(
            expense(title = "Pathao", amountMinor = 241850, date = today),
        )
        val viewModel = createViewModel(expenses = expenses, budget = budget)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.budgetState)
        assertEquals(BudgetStatus.Under, state.budgetState!!.status)
        assertTrue(state.budgetState!!.remainingLabel.contains("left"))
    }

    @Test
    fun noBudgetProgressWhenNoBudget() = runTest {
        val viewModel = createViewModel(expenses = listOf(expense(date = today)))
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.budgetState)
    }

    @Test
    fun categorySpendingLimitedToTopThree() = runTest {
        val allCategories = categories + listOf(category(5, "Shopping"))
        val expenses = listOf(
            expense(title = "Food", amountMinor = 50000, categoryId = 1, date = today),
            expense(title = "Ride", amountMinor = 30000, categoryId = 3, date = today),
            expense(title = "Internet", amountMinor = 20000, categoryId = 9, date = today),
            expense(title = "Daraz", amountMinor = 10000, categoryId = 5, date = today),
        )
        val viewModel = createViewModel(expenses = expenses, categories = allCategories)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(3, state.categorySpending.size)
        assertEquals("Food & Dining", state.categorySpending[0].name)
    }

    @Test
    fun categoryPercentageCalculatedCorrectly() = runTest {
        val expenses = listOf(
            expense(title = "Food", amountMinor = 75000, categoryId = 1, date = today),
            expense(title = "Ride", amountMinor = 25000, categoryId = 3, date = today),
        )
        val viewModel = createViewModel(expenses = expenses)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("75%", state.categorySpending[0].percent)
        assertEquals("25%", state.categorySpending[1].percent)
    }

    @Test
    fun recentTransactionsShowMerchantOrTitle() = runTest {
        val expenses = listOf(
            expense(title = "Ride", merchant = "Pathao", date = today),
            expense(title = "Lunch momo", merchant = null, date = today),
        )
        val viewModel = createViewModel(expenses = expenses)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Pathao", state.recentTransactions[0].merchant)
        assertEquals("Lunch momo", state.recentTransactions[1].merchant)
    }

    @Test
    fun monthLabelFormattedCorrectly() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.monthLabel.contains(currentMonth.year.toString()))
    }

    @Test
    fun quickAddItemsContainNepalMerchants() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val items = viewModel.uiState.value.quickAddItems
        assertTrue(items.contains("Pathao"))
        assertTrue(items.contains("Daraz"))
        assertTrue(items.contains("NTC/Ncell"))
        assertTrue(items.contains("eSewa/Khalti"))
    }

    private fun createViewModel(
        expenses: List<com.pulsefinance.domain.model.Expense> = emptyList(),
        budget: Budget? = null,
        categories: List<com.pulsefinance.domain.model.Category> = this.categories,
    ): DashboardViewModel {
        val expenseRepo = FakeExpenseRepository(expenses)
        val categoryRepo = FakeCategoryRepository(categories)
        val budgetRepo = FakeBudgetRepository(budget)
        val recurringRepo = FakeRecurringRuleRepository()
        val calculateBudgetProgress = CalculateBudgetProgressUseCase()
        val observeDashboard = ObserveDashboardUseCase(
            expenseRepository = expenseRepo,
            categoryRepository = categoryRepo,
            budgetRepository = budgetRepo,
            calculateBudgetProgress = calculateBudgetProgress,
        )
        val generateRecurring = GenerateDueRecurringExpensesUseCase(recurringRepo, expenseRepo)
        return DashboardViewModel(observeDashboard, generateRecurring)
    }
}
