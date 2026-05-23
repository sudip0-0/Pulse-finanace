package com.pulsefinance.domain.repository

import com.pulsefinance.domain.model.Budget
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun observeBudgetForMonth(month: YearMonth): Flow<Budget?>
    suspend fun getBudgetForMonth(month: YearMonth): Budget?
    suspend fun saveBudget(budget: Budget): Long
}
