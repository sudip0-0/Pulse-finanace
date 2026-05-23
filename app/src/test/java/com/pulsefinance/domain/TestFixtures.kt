package com.pulsefinance.domain

import com.pulsefinance.domain.model.Budget
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.KeywordMatchType
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.model.PaymentMethod
import com.pulsefinance.domain.model.RecurringFrequency
import com.pulsefinance.domain.model.RecurringRule
import com.pulsefinance.domain.model.TransactionFilters
import com.pulsefinance.domain.repository.BudgetRepository
import com.pulsefinance.domain.repository.CategoryKeywordRepository
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository
import com.pulsefinance.domain.repository.RecurringRuleRepository
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal val fixedInstant: Instant = Instant.parse("2026-05-23T10:15:30Z")

internal fun category(id: Long, name: String): Category {
    return Category(
        id = id,
        name = name,
        iconKey = name.lowercase().replace(" ", "_"),
        colorHex = "#FFFFFF",
        sortOrder = id.toInt(),
        isDefault = true,
        isArchived = false,
    )
}

internal fun expense(
    id: Long = 0,
    title: String = "Pathao ride",
    merchant: String? = "Pathao",
    amountMinor: Long = 28000,
    categoryId: Long = 3,
    date: LocalDate = LocalDate.of(2026, 5, 23),
    note: String? = null,
    recurringRuleId: Long? = null,
): Expense {
    return Expense(
        id = id,
        title = title,
        merchant = merchant,
        amount = Money(amountMinor),
        categoryId = categoryId,
        paymentMethod = PaymentMethod.Cash,
        expenseDate = date,
        note = note,
        isRecurringGenerated = recurringRuleId != null,
        recurringRuleId = recurringRuleId,
        createdAt = fixedInstant,
        updatedAt = fixedInstant,
    )
}

internal fun recurringRule(
    id: Long = 1,
    nextDueDate: LocalDate = LocalDate.of(2026, 5, 1),
    startDate: LocalDate = nextDueDate,
): RecurringRule {
    return RecurringRule(
        id = id,
        title = "WorldLink internet",
        merchant = "WorldLink",
        amount = Money(150000),
        categoryId = 9,
        frequency = RecurringFrequency.Monthly,
        interval = 1,
        startDate = startDate,
        nextDueDate = nextDueDate,
        endDate = null,
        isActive = true,
        createdAt = fixedInstant,
        updatedAt = fixedInstant,
    )
}

internal class FakeCategoryRepository(
    categories: List<Category>,
) : CategoryRepository {
    private val categoriesFlow = MutableStateFlow(categories)

    override fun observeCategories(): Flow<List<Category>> = categoriesFlow
    override suspend fun getCategory(categoryId: Long): Category? = categoriesFlow.value.firstOrNull { it.id == categoryId }
    override suspend fun getCategoryByName(name: String): Category? = categoriesFlow.value.firstOrNull { it.name == name }
}

internal class FakeKeywordRepository(
    private val keywords: List<CategoryKeyword>,
) : CategoryKeywordRepository {
    override suspend fun getKeywords(): List<CategoryKeyword> = keywords
}

internal class FakeExpenseRepository(
    expenses: List<Expense> = emptyList(),
    private val previousMerchantCategoryId: Long? = null,
) : ExpenseRepository {
    private val expensesFlow = MutableStateFlow(expenses)
    private var nextId = (expenses.maxOfOrNull { it.id } ?: 0L) + 1L

    override suspend fun addExpense(expense: Expense): Long {
        val id = nextId++
        expensesFlow.value = expensesFlow.value + expense.copy(id = id)
        return id
    }

    override suspend fun updateExpense(expense: Expense) {
        expensesFlow.value = expensesFlow.value.map { if (it.id == expense.id) expense else it }
    }

    override suspend fun deleteExpense(expenseId: Long) {
        expensesFlow.value = expensesFlow.value.filterNot { it.id == expenseId }
    }

    override suspend fun getExpense(expenseId: Long): Expense? = expensesFlow.value.firstOrNull { it.id == expenseId }

    override suspend fun hasGeneratedExpenseForRecurringRule(ruleId: Long, date: LocalDate): Boolean {
        return expensesFlow.value.any { it.recurringRuleId == ruleId && it.expenseDate == date && it.isRecurringGenerated }
    }

    override suspend fun findPreviousCategoryIdForMerchant(merchant: String): Long? {
        return previousMerchantCategoryId ?: expensesFlow.value
            .filter { it.merchant.equals(merchant, ignoreCase = true) }
            .maxByOrNull { it.expenseDate }
            ?.categoryId
    }

    override fun observeExpensesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        return expensesFlow.map { expenses -> expenses.filter { it.expenseDate in startDate..endDate } }
    }

    override fun observeTransactions(filters: TransactionFilters): Flow<List<Expense>> {
        return expensesFlow.map { expenses ->
            expenses.filter { expense ->
                val afterStart = filters.startDate?.let { !expense.expenseDate.isBefore(it) } ?: true
                val beforeEnd = filters.endDate?.let { !expense.expenseDate.isAfter(it) } ?: true
                val categoryMatches = filters.categoryId?.let { expense.categoryId == it } ?: true
                val searchMatches = filters.searchQuery?.let { query ->
                    listOf(expense.title, expense.merchant.orEmpty(), expense.note.orEmpty())
                        .any { it.contains(query, ignoreCase = true) }
                } ?: true
                afterStart && beforeEnd && categoryMatches && searchMatches
            }
        }
    }

    override fun observeRecentExpenses(limit: Int): Flow<List<Expense>> {
        return expensesFlow.map { expenses ->
            expenses.sortedWith(compareByDescending<Expense> { it.expenseDate }.thenByDescending { it.createdAt }).take(limit)
        }
    }
}

internal class FakeBudgetRepository(
    private val budget: Budget?,
) : BudgetRepository {
    override fun observeBudgetForMonth(month: YearMonth): Flow<Budget?> = MutableStateFlow(budget?.takeIf { it.month == month })
    override suspend fun getBudgetForMonth(month: YearMonth): Budget? = budget?.takeIf { it.month == month }
    override suspend fun saveBudget(budget: Budget): Long = budget.id
}

internal class FakeRecurringRuleRepository(
    rules: List<RecurringRule>,
) : RecurringRuleRepository {
    private val rulesFlow = MutableStateFlow(rules)

    override suspend fun addRule(rule: RecurringRule): Long = rule.id
    override suspend fun updateRule(rule: RecurringRule) {
        rulesFlow.value = rulesFlow.value.map { if (it.id == rule.id) rule else it }
    }

    override fun observeActiveRules(): Flow<List<RecurringRule>> = rulesFlow.map { rules -> rules.filter { it.isActive } }
    override suspend fun getActiveRulesDueOnOrBefore(date: LocalDate): List<RecurringRule> {
        return rulesFlow.value.filter { it.isActive && !it.nextDueDate.isAfter(date) }
    }
}
