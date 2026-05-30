package com.pulsefinance.presentation.transactions

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.expense
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.usecase.DeleteExpenseUseCase
import com.pulsefinance.domain.usecase.ObserveTransactionsUseCase
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val categories = listOf(
        category(1, "Food & Dining"),
        category(3, "Transport"),
    )
    private val expenses = listOf(
        expense(id = 1, title = "Pathao ride", merchant = "Pathao", amountMinor = 28000, categoryId = 3, date = LocalDate.of(2026, 5, 23)),
        expense(id = 2, title = "Lunch momo", merchant = "Local shop", amountMinor = 45000, categoryId = 1, date = LocalDate.of(2026, 5, 22)),
        expense(id = 3, title = "Daraz order", merchant = "Daraz", amountMinor = 120000, categoryId = 3, date = LocalDate.of(2026, 5, 21)),
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
    ): TransactionsViewModel {
        val categoryRepository = FakeCategoryRepository(categories)
        return TransactionsViewModel(
            observeTransactions = ObserveTransactionsUseCase(expenseRepository),
            deleteExpenseUseCase = DeleteExpenseUseCase(expenseRepository),
            categoryRepository = categoryRepository,
        )
    }

    @Test
    fun `loads all transactions on init`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(3, vm.uiState.value.transactions.size)
        assertEquals(false, vm.uiState.value.isLoading)
    }

    @Test
    fun `search filters transactions by title`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onSearchChanged("Pathao")
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.transactions.size)
        assertEquals("Pathao", vm.uiState.value.transactions[0].merchant)
    }

    @Test
    fun `search filters transactions by amount`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onSearchChanged("450")
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.transactions.size)
        assertEquals("Lunch momo", vm.uiState.value.transactions[0].title)
    }

    @Test
    fun `category filter shows only matching transactions`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onCategoryFilterSelected(1) // Food & Dining
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.transactions.size)
        assertEquals("Food & Dining", vm.uiState.value.transactions[0].categoryName)
    }

    @Test
    fun `toggling same category filter clears it`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onCategoryFilterSelected(3)
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.transactions.size)

        vm.onCategoryFilterSelected(3) // toggle off
        advanceUntilIdle()
        assertEquals(3, vm.uiState.value.transactions.size)
    }

    @Test
    fun `delete removes transaction from list`() = runTest {
        val expenseRepo = FakeExpenseRepository(expenses)
        val vm = createViewModel(expenseRepo)
        advanceUntilIdle()

        val toDelete = vm.uiState.value.transactions[0]
        vm.onDeleteRequested(toDelete)
        assertEquals(toDelete.id, vm.uiState.value.deleteConfirmation?.expenseId)

        vm.onDeleteConfirmed()
        advanceUntilIdle()

        assertNull(vm.uiState.value.deleteConfirmation)
        assertEquals(2, vm.uiState.value.transactions.size)
    }

    @Test
    fun `delete dismiss clears confirmation`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val toDelete = vm.uiState.value.transactions[0]
        vm.onDeleteRequested(toDelete)
        vm.onDeleteDismissed()

        assertNull(vm.uiState.value.deleteConfirmation)
        assertEquals(3, vm.uiState.value.transactions.size)
    }

    @Test
    fun `clear filters resets search and category`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onSearchChanged("xyz")
        vm.onCategoryFilterSelected(1)
        advanceUntilIdle()

        vm.onClearFilters()
        advanceUntilIdle()

        assertEquals("", vm.uiState.value.searchQuery)
        assertNull(vm.uiState.value.selectedCategoryId)
        assertEquals(3, vm.uiState.value.transactions.size)
    }

    @Test
    fun `categories are loaded on init`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(2, vm.uiState.value.categories.size)
    }

    @Test
    fun `empty state shows when no transactions match`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onSearchChanged("nonexistent")
        advanceUntilIdle()

        assertTrue(vm.uiState.value.isEmpty)
        assertTrue(vm.uiState.value.hasActiveFilters)
    }
}
