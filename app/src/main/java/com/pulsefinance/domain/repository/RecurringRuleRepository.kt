package com.pulsefinance.domain.repository

import com.pulsefinance.domain.model.RecurringRule
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface RecurringRuleRepository {
    suspend fun addRule(rule: RecurringRule): Long
    suspend fun updateRule(rule: RecurringRule)
    fun observeActiveRules(): Flow<List<RecurringRule>>
    suspend fun getActiveRulesDueOnOrBefore(date: LocalDate): List<RecurringRule>
}
