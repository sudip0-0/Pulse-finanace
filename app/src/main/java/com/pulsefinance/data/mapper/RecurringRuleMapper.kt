package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.RecurringRuleEntity
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.model.RecurringFrequency
import com.pulsefinance.domain.model.RecurringRule
import com.pulsefinance.data.local.entity.RecurringFrequency as EntityFrequency

fun RecurringRuleEntity.toDomain(): RecurringRule = RecurringRule(
    id = id,
    title = title,
    merchant = merchant,
    amount = Money(amountMinor = amountMinor, currencyCode = currencyCode),
    categoryId = categoryId,
    frequency = frequency.toDomain(),
    interval = interval,
    startDate = startDate,
    nextDueDate = nextDueDate,
    endDate = endDate,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun RecurringRule.toEntity(): RecurringRuleEntity = RecurringRuleEntity(
    id = id,
    title = title,
    merchant = merchant,
    amountMinor = amount.amountMinor,
    currencyCode = amount.currencyCode,
    categoryId = categoryId,
    frequency = frequency.toEntity(),
    interval = interval,
    startDate = startDate,
    nextDueDate = nextDueDate,
    endDate = endDate,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

private fun EntityFrequency.toDomain(): RecurringFrequency = when (this) {
    EntityFrequency.Weekly -> RecurringFrequency.Weekly
    EntityFrequency.Monthly -> RecurringFrequency.Monthly
    EntityFrequency.Yearly -> RecurringFrequency.Yearly
}

private fun RecurringFrequency.toEntity(): EntityFrequency = when (this) {
    RecurringFrequency.Weekly -> EntityFrequency.Weekly
    RecurringFrequency.Monthly -> EntityFrequency.Monthly
    RecurringFrequency.Yearly -> EntityFrequency.Yearly
}
