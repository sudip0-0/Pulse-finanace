package com.pulsefinance.data.repository

import com.pulsefinance.data.local.dao.RecurringRuleDao
import com.pulsefinance.data.mapper.toDomain
import com.pulsefinance.data.mapper.toEntity
import com.pulsefinance.domain.model.RecurringRule
import com.pulsefinance.domain.repository.RecurringRuleRepository
import java.time.LocalDate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RecurringRuleRepositoryImpl(
    private val recurringRuleDao: RecurringRuleDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RecurringRuleRepository {

    override suspend fun addRule(rule: RecurringRule): Long = withContext(ioDispatcher) {
        recurringRuleDao.insert(rule.toEntity())
    }

    override suspend fun updateRule(rule: RecurringRule) = withContext(ioDispatcher) {
        recurringRuleDao.update(rule.toEntity())
    }

    override fun observeActiveRules(): Flow<List<RecurringRule>> =
        recurringRuleDao.observeActiveRules()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override suspend fun getActiveRulesDueOnOrBefore(date: LocalDate): List<RecurringRule> =
        withContext(ioDispatcher) {
            recurringRuleDao.getActiveRulesDueOnOrBefore(date).map { it.toDomain() }
        }
}
