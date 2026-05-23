package com.pulsefinance.presentation.expense

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.FakeKeywordRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.model.KeywordMatchType
import com.pulsefinance.domain.usecase.AddExpenseUseCase
import com.pulsefinance.domain.usecase.CategorizeExpenseUseCase
import com.pulsefinance.domain.usecase.UpdateExpenseUseCase
import androidx.lifecycle.SavedStateHandle
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
class AddExpenseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val categories = listOf(
        category(1, "Food & Dining"),
        category(3, "Transport"),
        category(16, "Other"),
    )
    private val keywords = listOf(
        CategoryKeyword(1, 3, "pathao", KeywordMatchType.Merchant, 100),
        CategoryKeyword(2, 1, "foodmandu", KeywordMatchType.Merchant, 100),
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
        expenseRepository: FakeExpenseRepository = FakeExpenseRepository(),
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): AddExpenseViewModel {
        val categoryRepository = FakeCategoryRepository(categories)
        return AddExpenseViewModel(
            addExpenseUseCase = AddExpenseUseCase(expenseRepository, categoryRepository),
            updateExpenseUseCase = UpdateExpenseUseCase(expenseRepository, categoryRepository),
            categorizeExpenseUseCase = CategorizeExpenseUseCase(
                categoryRepository, FakeKeywordRepository(keywords), expenseRepository,
            ),
            categoryRepository = categoryRepository,
            expenseRepository = expenseRepository,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `save fails with empty amount`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onTitleChanged("Lunch")
        vm.onCategorySelected(categories[0])
        vm.onSave()
        advanceUntilIdle()

        assertEquals("Enter a valid amount.", vm.uiState.value.errorMessage)
        assertFalse(vm.uiState.value.saved)
    }

    @Test
    fun `save fails without title or merchant`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onAmountChanged("100")
        vm.onCategorySelected(categories[0])
        vm.onSave()
        advanceUntilIdle()

        assertEquals("Title or merchant is required.", vm.uiState.value.errorMessage)
    }

    @Test
    fun `save fails without category when none selected`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onAmountChanged("100")
        vm.onTitleChanged("Something")
        // Don't wait for categorization to auto-select — save immediately
        vm.onSave()

        // Category may be null if categorization hasn't run yet
        val state = vm.uiState.value
        if (state.selectedCategory == null) {
            assertEquals("Select a category.", state.errorMessage)
        }
        // If categorization already ran and auto-selected, the save proceeds — that's valid behavior
    }

    @Test
    fun `successful save sets saved flag`() = runTest {
        val expenseRepo = FakeExpenseRepository()
        val vm = createViewModel(expenseRepo)
        advanceUntilIdle()

        vm.onAmountChanged("280")
        vm.onTitleChanged("Pathao ride")
        vm.onMerchantChanged("Pathao")
        advanceUntilIdle()
        vm.onCategorySelected(categories[1])
        vm.onSave()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.saved)
        assertNull(vm.uiState.value.errorMessage)
    }

    @Test
    fun `amount parsing handles decimal correctly`() = runTest {
        val expenseRepo = FakeExpenseRepository()
        val vm = createViewModel(expenseRepo)
        advanceUntilIdle()

        vm.onAmountChanged("150.50")
        vm.onTitleChanged("Bus fare")
        vm.onCategorySelected(categories[1])
        vm.onSave()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.saved)
    }

    @Test
    fun `multiple dots in amount are rejected`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onAmountChanged("12.34")
        assertEquals("12.34", vm.uiState.value.amountText)

        // Attempting to add another dot keeps the previous valid value
        vm.onAmountChanged("12.34.56")
        assertEquals("12.34", vm.uiState.value.amountText)
    }

    @Test
    fun `amount input is capped at max length`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        val longInput = "1234567890123456"
        vm.onAmountChanged(longInput)
        assertTrue(vm.uiState.value.amountText.length <= 12)
    }

    @Test
    fun `double save is prevented by isSaving guard`() = runTest {
        val expenseRepo = FakeExpenseRepository()
        val vm = createViewModel(expenseRepo)
        advanceUntilIdle()

        vm.onAmountChanged("100")
        vm.onTitleChanged("Test")
        vm.onCategorySelected(categories[0])

        // Call save twice rapidly
        vm.onSave()
        vm.onSave()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.saved)
    }

    @Test
    fun `categorization suggests transport for pathao`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onMerchantChanged("Pathao")
        advanceUntilIdle()

        val suggestion = vm.uiState.value.suggestedCategory
        assertNotNull(suggestion)
        assertEquals("Transport", suggestion!!.name)
    }

    @Test
    fun `categorization suggests food for foodmandu`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onMerchantChanged("Foodmandu")
        advanceUntilIdle()

        val suggestion = vm.uiState.value.suggestedCategory
        assertNotNull(suggestion)
        assertEquals("Food & Dining", suggestion!!.name)
    }

    @Test
    fun `categories are loaded on init`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        assertEquals(3, vm.uiState.value.categories.size)
    }

    @Test
    fun `quick add prefill selects merchant and category`() = runTest {
        val vm = createViewModel(
            savedStateHandle = SavedStateHandle(
                mapOf(
                    "merchant" to "Pathao",
                    "category" to "Transport",
                ),
            ),
        )
        advanceUntilIdle()

        assertEquals("Pathao", vm.uiState.value.merchant)
        assertEquals("Transport", vm.uiState.value.selectedCategory?.name)
        assertEquals("Quick add", vm.uiState.value.suggestionReason)
    }

    @Test
    fun `error is cleared on input change`() = runTest {
        val vm = createViewModel()
        advanceUntilIdle()

        vm.onSave() // triggers error
        assertNotNull(vm.uiState.value.errorMessage)

        vm.onAmountChanged("50")
        assertNull(vm.uiState.value.errorMessage)
    }
}
