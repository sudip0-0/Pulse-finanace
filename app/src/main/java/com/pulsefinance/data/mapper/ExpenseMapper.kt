package com.pulsefinance.data.mapper

import com.pulsefinance.data.local.entity.ExpenseEntity
import com.pulsefinance.domain.model.Expense
import com.pulsefinance.domain.model.Money
import com.pulsefinance.domain.model.PaymentMethod

fun ExpenseEntity.toDomain(): Expense = Expense(
    id = id,
    title = title,
    merchant = merchant,
    amount = Money(amountMinor = amountMinor, currencyCode = currencyCode),
    categoryId = categoryId,
    paymentMethod = paymentMethod?.let { name ->
        PaymentMethod.entries.firstOrNull { it.name.equals(name, ignoreCase = true) }
    },
    expenseDate = expenseDate,
    note = note,
    isRecurringGenerated = isRecurringGenerated,
    recurringRuleId = recurringRuleId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    title = title,
    merchant = merchant,
    amountMinor = amount.amountMinor,
    currencyCode = amount.currencyCode,
    categoryId = categoryId,
    paymentMethod = paymentMethod?.name,
    expenseDate = expenseDate,
    note = note,
    isRecurringGenerated = isRecurringGenerated,
    recurringRuleId = recurringRuleId,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
