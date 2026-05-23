package com.pulsefinance.data.repository

import com.pulsefinance.data.local.dao.BudgetDao
import com.pulsefinance.data.mapper.toDomain
import com.pulsefinance.data.mapper.toEntity
import com.pulsefinance.domain.model.Budget
import com.pulsefinance.domain.repository.BudgetRepository
import java.time.YearMonth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : BudgetRepository {

    override fun observeBudgetForMonth(month: YearMonth): Flow<Budget?> =
        budgetDao.observeBudgetForMonth(month)
            .map { it?.toDomain() }
            .flowOn(ioDispatcher)

    override suspend fun getBudgetForMonth(month: YearMonth): Budget? = withContext(ioDispatcher) {
        budgetDao.getBudgetForMonth(month)?.toDomain()
    }

    override suspend fun saveBudget(budget: Budget): Long = withContext(ioDispatcher) {
        val existing = budgetDao.getBudgetForMonth(budget.month)
        if (existing != null) {
            val updated = budget.toEntity().copy(id = existing.id)
            budgetDao.update(updated)
            existing.id
        } else {
            budgetDao.insert(budget.toEntity())
        }
    }
}
