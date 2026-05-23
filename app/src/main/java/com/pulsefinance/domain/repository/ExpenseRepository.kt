package com.pulsefinance.domain.repository

import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.TransactionFilters
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    suspend fun addExpense(expense: Expense): Long
    suspend fun updateExpense(expense: Expense)
    suspend fun deleteExpense(expenseId: Long)
    suspend fun getExpense(expenseId: Long): Expense?
    suspend fun hasGeneratedExpenseForRecurringRule(ruleId: Long, date: LocalDate): Boolean
    suspend fun findPreviousCategoryIdForMerchant(merchant: String): Long?
    fun observeExpensesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>
    fun observeTransactions(filters: TransactionFilters): Flow<List<Expense>>
    fun observeRecentExpenses(limit: Int): Flow<List<Expense>>
}
