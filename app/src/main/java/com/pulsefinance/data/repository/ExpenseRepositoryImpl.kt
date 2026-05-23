package com.pulsefinance.data.repository

import com.pulsefinance.data.local.dao.ExpenseDao
import com.pulsefinance.data.mapper.toDomain
import com.pulsefinance.data.mapper.toEntity
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.TransactionFilters
import com.pulsefinance.domain.repository.ExpenseRepository
import java.time.LocalDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ExpenseRepository {

    override suspend fun addExpense(expense: Expense): Long = withContext(ioDispatcher) {
        expenseDao.insert(expense.toEntity())
    }

    override suspend fun updateExpense(expense: Expense) = withContext(ioDispatcher) {
        expenseDao.update(expense.toEntity())
    }

    override suspend fun deleteExpense(expenseId: Long) = withContext(ioDispatcher) {
        expenseDao.deleteById(expenseId)
    }

    override suspend fun getExpense(expenseId: Long): Expense? = withContext(ioDispatcher) {
        expenseDao.getExpenseById(expenseId)?.toDomain()
    }

    override suspend fun hasGeneratedExpenseForRecurringRule(ruleId: Long, date: LocalDate): Boolean =
        withContext(ioDispatcher) {
            expenseDao.hasGeneratedExpenseForRule(ruleId, date)
        }

    override suspend fun findPreviousCategoryIdForMerchant(merchant: String): Long? =
        withContext(ioDispatcher) {
            expenseDao.findPreviousCategoryIdForMerchant(merchant)
        }

    override fun observeExpensesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> =
        expenseDao.observeExpensesBetween(startDate, endDate)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override fun observeTransactions(filters: TransactionFilters): Flow<List<Expense>> =
        expenseDao.observeFilteredExpenses(
            startDate = filters.startDate,
            endDate = filters.endDate,
            categoryId = filters.categoryId,
            searchQuery = filters.searchQuery,
        )
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override fun observeRecentExpenses(limit: Int): Flow<List<Expense>> =
        expenseDao.observeRecentExpenses(limit)
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)
}
