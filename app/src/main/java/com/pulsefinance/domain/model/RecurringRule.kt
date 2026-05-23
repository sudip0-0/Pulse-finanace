package com.pulsefinance.domain.model

import java.time.Instant
import java.time.LocalDate

data class RecurringRule(
    val id: Long = 0,
    val title: String,
    val merchant: String?,
    val amount: Money,
    val categoryId: Long,
    val frequency: RecurringFrequency,
    val interval: Int,
    val startDate: LocalDate,
    val nextDueDate: LocalDate,
    val endDate: LocalDate?,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(id >= 0) { "Recurring rule id cannot be negative." }
        require(title.isNotBlank()) { "Recurring title is required." }
        require(amount.isPositive()) { "Recurring amount must be positive." }
        require(categoryId > 0) { "Recurring category is required." }
        require(interval > 0) { "Recurring interval must be positive." }
        require(!nextDueDate.isBefore(startDate)) { "Next due date cannot be before start date." }
        require(endDate == null || !endDate.isBefore(startDate)) { "End date cannot be before start date." }
    }

    fun nextDateAfter(date: LocalDate): LocalDate {
        return when (frequency) {
            RecurringFrequency.Weekly -> date.plusWeeks(interval.toLong())
            RecurringFrequency.Monthly -> nextMonthlyDateAfter(date)
            RecurringFrequency.Yearly -> nextYearlyDateAfter(date)
        }
    }

    private fun nextMonthlyDateAfter(date: LocalDate): LocalDate {
        val nextMonth = date.plusMonths(interval.toLong())
        return when {
            startDate.isLastDayOfMonth() -> nextMonth.withDayOfMonth(nextMonth.lengthOfMonth())
            startDate.dayOfMonth > nextMonth.lengthOfMonth() -> nextMonth.withDayOfMonth(nextMonth.lengthOfMonth())
            else -> nextMonth.withDayOfMonth(startDate.dayOfMonth)
        }
    }

    private fun nextYearlyDateAfter(date: LocalDate): LocalDate {
        val nextYear = date.plusYears(interval.toLong())
        return if (startDate.monthValue == 2 && startDate.dayOfMonth == 29 && !nextYear.isLeapYear) {
            nextYear.withDayOfMonth(28)
        } else {
            nextYear
        }
    }
}

private fun LocalDate.isLastDayOfMonth(): Boolean = dayOfMonth == lengthOfMonth()

enum class RecurringFrequency {
    Weekly,
    Monthly,
    Yearly,
}
