package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.FakeExpenseRepository
import com.pulsefinance.domain.FakeKeywordRepository
import com.pulsefinance.domain.category
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.KeywordMatchType
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CategorizeExpenseUseCaseTest {
    private val categories = listOf(
        category(1, "Food & Dining"),
        category(2, "Groceries"),
        category(3, "Transport"),
        category(16, "Other"),
    )

    @Test
    fun exactMerchantMatchWins() = runBlocking {
        val useCase = useCase(
            keywords = listOf(
                CategoryKeyword(1, 3, "pathao", KeywordMatchType.Merchant, 100),
                CategoryKeyword(2, 1, "ride", KeywordMatchType.Keyword, 80),
            ),
        )

        val result = useCase(CategorizationInput(title = "Ride to Baneshwor", merchant = "Pathao"))

        val value = (result as DomainResult.Success).value
        assertEquals("Transport", value.category.name)
        assertEquals(CategorizationReason.ExactMerchant, value.reason)
    }

    @Test
    fun merchantKeywordMatchesSuffixText() = runBlocking {
        val useCase = useCase(
            keywords = listOf(CategoryKeyword(1, 3, "pathao", KeywordMatchType.Merchant, 100)),
        )

        val result = useCase(CategorizationInput(title = "Ride", merchant = "Pathao ride"))

        val value = (result as DomainResult.Success).value
        assertEquals("Transport", value.category.name)
        assertEquals(CategorizationReason.ExactMerchant, value.reason)
    }

    @Test
    fun weightedKeywordMatchHandlesPunctuationAndCase() = runBlocking {
        val useCase = useCase(
            keywords = listOf(
                CategoryKeyword(1, 1, "momo", KeywordMatchType.Keyword, 70),
                CategoryKeyword(2, 2, "kirana", KeywordMatchType.Keyword, 80),
            ),
        )

        val result = useCase(CategorizationInput(title = "Lunch: MOMO!!", merchant = null))

        val value = (result as DomainResult.Success).value
        assertEquals("Food & Dining", value.category.name)
        assertEquals(CategorizationReason.Keyword, value.reason)
    }

    @Test
    fun previousMerchantCategoryIsUsedBeforeOtherFallback() = runBlocking {
        val useCase = CategorizeExpenseUseCase(
            categoryRepository = FakeCategoryRepository(categories),
            keywordRepository = FakeKeywordRepository(emptyList()),
            expenseRepository = FakeExpenseRepository(previousMerchantCategoryId = 2),
        )

        val result = useCase(CategorizationInput(title = "Weekly shop", merchant = "Local store"))

        val value = (result as DomainResult.Success).value
        assertEquals("Groceries", value.category.name)
        assertEquals(CategorizationReason.PreviousMerchant, value.reason)
    }

    @Test
    fun previousMerchantCategoryWinsBeforeWeakKeyword() = runBlocking {
        val useCase = CategorizeExpenseUseCase(
            categoryRepository = FakeCategoryRepository(categories),
            keywordRepository = FakeKeywordRepository(
                listOf(CategoryKeyword(1, 1, "snack", KeywordMatchType.Keyword, 30)),
            ),
            expenseRepository = FakeExpenseRepository(previousMerchantCategoryId = 2),
        )

        val result = useCase(CategorizationInput(title = "snack", merchant = "Local store"))

        val value = (result as DomainResult.Success).value
        assertEquals("Groceries", value.category.name)
        assertEquals(CategorizationReason.PreviousMerchant, value.reason)
    }

    @Test
    fun fallsBackToOther() = runBlocking {
        val result = useCase(emptyList())(CategorizationInput(title = "Unknown", merchant = null))

        val value = (result as DomainResult.Success).value
        assertEquals("Other", value.category.name)
        assertEquals(CategorizationReason.FallbackOther, value.reason)
    }

    @Test
    fun equalKeywordWeightUsesCategorySortOrderTieBreak() = runBlocking {
        val result = useCase(
            listOf(
                CategoryKeyword(1, 2, "market", KeywordMatchType.Keyword, 80),
                CategoryKeyword(2, 1, "market", KeywordMatchType.Keyword, 80),
            ),
        )(CategorizationInput(title = "market", merchant = null))

        val value = (result as DomainResult.Success).value
        assertEquals("Food & Dining", value.category.name)
    }

    // Nepal-specific merchant categorization tests

    @Test
    fun categorizesEsewaAsWalletTransfers() = runBlocking {
        val nepalCategories = categories + listOf(category(5, "Wallet & Transfers"))
        val useCase = CategorizeExpenseUseCase(
            categoryRepository = FakeCategoryRepository(nepalCategories),
            keywordRepository = FakeKeywordRepository(
                listOf(CategoryKeyword(1, 5, "esewa", KeywordMatchType.Merchant, 100)),
            ),
            expenseRepository = FakeExpenseRepository(),
        )

        val result = useCase(CategorizationInput(title = "Payment", merchant = "eSewa"))
        val value = (result as DomainResult.Success).value
        assertEquals("Wallet & Transfers", value.category.name)
    }

    @Test
    fun categorizesKhaltiAsWalletTransfers() = runBlocking {
        val nepalCategories = categories + listOf(category(5, "Wallet & Transfers"))
        val useCase = CategorizeExpenseUseCase(
            categoryRepository = FakeCategoryRepository(nepalCategories),
            keywordRepository = FakeKeywordRepository(
                listOf(CategoryKeyword(1, 5, "khalti", KeywordMatchType.Merchant, 100)),
            ),
            expenseRepository = FakeExpenseRepository(),
        )

        val result = useCase(CategorizationInput(title = "Transfer", merchant = "Khalti"))
        val value = (result as DomainResult.Success).value
        assertEquals("Wallet & Transfers", value.category.name)
    }

    @Test
    fun categorizesBhatBhateniAsGroceries() = runBlocking {
        val useCase = useCase(
            listOf(CategoryKeyword(1, 2, "bhatbhateni", KeywordMatchType.Merchant, 100)),
        )

        val result = useCase(CategorizationInput(title = "Weekly groceries", merchant = "Bhatbhateni"))
        val value = (result as DomainResult.Success).value
        assertEquals("Groceries", value.category.name)
    }

    @Test
    fun categorizesNtcAsMobileRecharge() = runBlocking {
        val nepalCategories = categories + listOf(category(6, "Mobile Recharge"))
        val useCase = CategorizeExpenseUseCase(
            categoryRepository = FakeCategoryRepository(nepalCategories),
            keywordRepository = FakeKeywordRepository(
                listOf(CategoryKeyword(1, 6, "ntc", KeywordMatchType.Keyword, 80)),
            ),
            expenseRepository = FakeExpenseRepository(),
        )

        val result = useCase(CategorizationInput(title = "NTC recharge", merchant = null))
        val value = (result as DomainResult.Success).value
        assertEquals("Mobile Recharge", value.category.name)
    }

    @Test
    fun categorizesWorldLinkAsInternetTv() = runBlocking {
        val nepalCategories = categories + listOf(category(9, "Internet & TV"))
        val useCase = CategorizeExpenseUseCase(
            categoryRepository = FakeCategoryRepository(nepalCategories),
            keywordRepository = FakeKeywordRepository(
                listOf(CategoryKeyword(1, 9, "worldlink", KeywordMatchType.Merchant, 100)),
            ),
            expenseRepository = FakeExpenseRepository(),
        )

        val result = useCase(CategorizationInput(title = "Monthly internet", merchant = "WorldLink"))
        val value = (result as DomainResult.Success).value
        assertEquals("Internet & TV", value.category.name)
    }

    @Test
    fun categorizesFoodmanduAsFoodDining() = runBlocking {
        val useCase = useCase(
            listOf(CategoryKeyword(1, 1, "foodmandu", KeywordMatchType.Merchant, 100)),
        )

        val result = useCase(CategorizationInput(title = "Dinner delivery", merchant = "Foodmandu"))
        val value = (result as DomainResult.Success).value
        assertEquals("Food & Dining", value.category.name)
    }

    private fun useCase(keywords: List<CategoryKeyword>): CategorizeExpenseUseCase {
        return CategorizeExpenseUseCase(
            categoryRepository = FakeCategoryRepository(categories),
            keywordRepository = FakeKeywordRepository(keywords),
            expenseRepository = FakeExpenseRepository(),
        )
    }
}
