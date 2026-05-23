package com.pulsefinance.domain.model

import java.time.Instant
import java.time.LocalDate

data class Expense(
    val id: Long = 0,
    val title: String,
    val merchant: String?,
    val amount: Money,
    val categoryId: Long,
    val paymentMethod: PaymentMethod?,
    val expenseDate: LocalDate,
    val note: String?,
    val isRecurringGenerated: Boolean = false,
    val recurringRuleId: Long? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(id >= 0) { "Expense id cannot be negative." }
        require(title.isNotBlank()) { "Expense title is required." }
        require(amount.isPositive()) { "Expense amount must be positive." }
        require(categoryId > 0) { "Expense category is required." }
    }
}

enum class PaymentMethod {
    Cash,
    Esewa,
    Khalti,
    Fonepay,
    Bank,
    Card,
}
