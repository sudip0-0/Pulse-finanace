package com.pulsefinance.presentation.receipt

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.FakeKeywordRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.KeywordMatchType
import com.pulsefinance.domain.model.TransactionFilters
import kotlinx.coroutines.flow.first
import com.pulsefinance.domain.repository.ReceiptImageInput
import com.pulsefinance.domain.repository.ReceiptTextRecognizer
import com.pulsefinance.domain.usecase.AddExpenseUseCase
import com.pulsefinance.domain.usecase.CategorizeExpenseUseCase
import com.pulsefinance.domain.usecase.ParseReceiptFromTextUseCase
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
class ScanReceiptViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val categories = listOf(
        category(1, "Food & Dining"),
        category(3, "Transport"),
    )

  private val pathaoReceipt = """
        Pathao Nepal
        Date: 23/05/2026
        TOTAL Rs 280.00
    """.trimIndent()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `processes receipt text into review state`() = runTest {
        val vm = createViewModel(FakeReceiptTextRecognizer(pathaoReceipt))
        advanceUntilIdle()

        vm.onReceiptTextForTest(pathaoReceipt)
        advanceUntilIdle()

        assertEquals(ScanReceiptStep.Review, vm.uiState.value.step)
        assertEquals("280", vm.uiState.value.amountText)
        assertEquals("Pathao Nepal", vm.uiState.value.merchant)
    }

    @Test
    fun `save adds expense when fields are valid`() = runTest {
        val expenseRepository = FakeExpenseRepository(emptyList())
        val vm = createViewModel(FakeReceiptTextRecognizer(pathaoReceipt), expenseRepository)
        advanceUntilIdle()

        vm.onReceiptTextForTest(pathaoReceipt)
        advanceUntilIdle()

        vm.onSave()
        advanceUntilIdle()

        assertTrue(vm.uiState.value.saved)
        assertEquals(1, expenseRepository.observeTransactions(TransactionFilters()).first().size)
    }

    private fun createViewModel(
        recognizer: ReceiptTextRecognizer,
        expenseRepository: FakeExpenseRepository = FakeExpenseRepository(emptyList()),
    ): ScanReceiptViewModel {
        val categoryRepository = FakeCategoryRepository(categories)
        val keywords = listOf(
            CategoryKeyword(1, 3, "pathao", KeywordMatchType.Merchant, 100),
        )
        return ScanReceiptViewModel(
            receiptTextRecognizer = recognizer,
            parseReceiptFromText = ParseReceiptFromTextUseCase(),
            categorizeExpenseUseCase = CategorizeExpenseUseCase(
                categoryRepository = categoryRepository,
                keywordRepository = FakeKeywordRepository(keywords),
                expenseRepository = expenseRepository,
            ),
            addExpenseUseCase = AddExpenseUseCase(expenseRepository, categoryRepository),
            categoryRepository = categoryRepository,
        )
    }

    private class FakeReceiptTextRecognizer(
        private val text: String,
    ) : ReceiptTextRecognizer {
        override suspend fun recognizeText(image: ReceiptImageInput): DomainResult<String> {
            return DomainResult.Success(text)
        }
    }
}
