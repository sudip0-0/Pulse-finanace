package com.pulsefinance.presentation.navigation

sealed class PulseRoute(val path: String) {
    data object Dashboard : PulseRoute("dashboard")
    data object Analytics : PulseRoute("analytics")
    data object Transactions : PulseRoute("transactions")
    data object AddExpense : PulseRoute("add_expense")
    data object EditExpense : PulseRoute("edit_expense/{expenseId}") {
        fun withId(expenseId: Long) = "edit_expense/$expenseId"
    }
    data object Recurring : PulseRoute("recurring")
    data object AddRecurringRule : PulseRoute("add_recurring_rule")
    data object EditRecurringRule : PulseRoute("edit_recurring_rule/{ruleId}") {
        fun withId(ruleId: Long) = "edit_recurring_rule/$ruleId"
    }
    data object Settings : PulseRoute("settings")
}
