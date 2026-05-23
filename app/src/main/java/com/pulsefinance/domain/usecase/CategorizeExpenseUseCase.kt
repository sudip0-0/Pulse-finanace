package com.pulsefinance.domain.usecase

import com.pulsefinance.domain.categorization.TextNormalizer
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.model.DomainError
import com.pulsefinance.domain.model.DomainResult
import com.pulsefinance.domain.model.KeywordMatchType
import com.pulsefinance.domain.repository.CategoryKeywordRepository
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.first

class CategorizeExpenseUseCase(
    private val categoryRepository: CategoryRepository,
    private val keywordRepository: CategoryKeywordRepository,
    private val expenseRepository: ExpenseRepository,
) {
    suspend operator fun invoke(input: CategorizationInput): DomainResult<CategorizationResult> {
        val categories = categoryRepository.observeCategoriesSnapshot()
        if (categories.isEmpty()) {
            return DomainResult.Failure(DomainError.NotFound("No categories are available."))
        }

        val categoryById = categories.associateBy { it.id }
        val other = categories.firstOrNull { it.name == OTHER_CATEGORY }
        val merchantText = TextNormalizer.normalize(input.merchant)
        val combinedText = TextNormalizer.normalize(input.title, input.merchant, input.note)
        val keywords = keywordRepository.getKeywords()

        findExactMerchantMatch(merchantText, keywords, categoryById)?.let { return DomainResult.Success(it) }
        findWeightedKeywordMatch(combinedText, keywords, categoryById, minimumWeight = STRONG_KEYWORD_WEIGHT)
            ?.let { return DomainResult.Success(it) }

        val previousCategoryId = input.merchant
            ?.takeIf { it.isNotBlank() }
            ?.let { expenseRepository.findPreviousCategoryIdForMerchant(it) }
        previousCategoryId?.let { id ->
            categoryById[id]?.let { category ->
                return DomainResult.Success(
                    CategorizationResult(
                        category = category,
                        reason = CategorizationReason.PreviousMerchant,
                        matchedText = input.merchant,
                    ),
                )
            }
        }

        findWeightedKeywordMatch(combinedText, keywords, categoryById, minimumWeight = 0)
            ?.let { return DomainResult.Success(it) }

        if (other != null) {
            return DomainResult.Success(
                CategorizationResult(
                    category = other,
                    reason = CategorizationReason.FallbackOther,
                    matchedText = null,
                ),
            )
        }

        return DomainResult.Failure(DomainError.NotFound("Other category is missing."))
    }

    private fun findExactMerchantMatch(
        merchantText: String,
        keywords: List<CategoryKeyword>,
        categoryById: Map<Long, Category>,
    ): CategorizationResult? {
        if (merchantText.isBlank()) return null
        return keywords
            .filter { it.matchType == KeywordMatchType.Merchant && keywordMatches(merchantText, TextNormalizer.normalize(it.keyword)) }
            .maxWithOrNull(keywordComparator(categoryById))
            ?.let { keyword ->
                categoryById[keyword.categoryId]?.let { category ->
                    CategorizationResult(category, CategorizationReason.ExactMerchant, keyword.keyword)
                }
            }
    }

    private fun findWeightedKeywordMatch(
        text: String,
        keywords: List<CategoryKeyword>,
        categoryById: Map<Long, Category>,
        minimumWeight: Int,
    ): CategorizationResult? {
        if (text.isBlank()) return null
        return keywords
            .filter { keyword -> keyword.weight >= minimumWeight && keywordMatches(text, TextNormalizer.normalize(keyword.keyword)) }
            .maxWithOrNull(keywordComparator(categoryById))
            ?.let { keyword ->
                categoryById[keyword.categoryId]?.let { category ->
                    CategorizationResult(category, CategorizationReason.Keyword, keyword.keyword)
                }
            }
    }

    private fun keywordMatches(text: String, keyword: String): Boolean {
        return keyword.isNotBlank() && Regex("(^|\\s)${Regex.escape(keyword)}(\\s|$)").containsMatchIn(text)
    }

    private fun keywordComparator(categoryById: Map<Long, Category>): Comparator<CategoryKeyword> {
        return Comparator { left, right ->
            val weightCompare = left.weight.compareTo(right.weight)
            if (weightCompare != 0) {
                weightCompare
            } else {
                val leftCategory = categoryById[left.categoryId]
                val rightCategory = categoryById[right.categoryId]
                val sortCompare = (rightCategory?.sortOrder ?: Int.MAX_VALUE)
                    .compareTo(leftCategory?.sortOrder ?: Int.MAX_VALUE)
                if (sortCompare != 0) {
                    sortCompare
                } else {
                    rightCategory?.name.orEmpty().compareTo(leftCategory?.name.orEmpty())
                }
            }
        }
    }

    private suspend fun CategoryRepository.observeCategoriesSnapshot(): List<Category> {
        return observeCategories().first()
    }

    private companion object {
        const val OTHER_CATEGORY = "Other"
        const val STRONG_KEYWORD_WEIGHT = 80
    }
}

data class CategorizationInput(
    val title: String,
    val merchant: String?,
    val note: String? = null,
)

data class CategorizationResult(
    val category: Category,
    val reason: CategorizationReason,
    val matchedText: String?,
)

enum class CategorizationReason {
    ExactMerchant,
    Keyword,
    PreviousMerchant,
    FallbackOther,
}
