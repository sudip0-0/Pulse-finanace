package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.BudgetEntity
import com.pulsefinance.domain.model.Budget
import com.pulsefinance.domain.model.Money

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    month = month,
    amount = Money(amountMinor = amountMinor, currencyCode = currencyCode),
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    month = month,
    amountMinor = amount.amountMinor,
    currencyCode = amount.currencyCode,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
