package com.pulsefinance.domain.model

import java.time.Instant
import java.time.YearMonth

data class Budget(
    val id: Long = 0,
    val month: YearMonth,
    val amount: Money,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(id >= 0) { "Budget id cannot be negative." }
        require(amount.isPositive()) { "Budget amount must be positive." }
    }
}
