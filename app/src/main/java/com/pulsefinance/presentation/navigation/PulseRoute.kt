package com.pulsefinance.presentation.navigation

sealed class PulseRoute(val path: String) {
    data object Dashboard : PulseRoute("dashboard")
    data object Analytics : PulseRoute("analytics")
    data object Transactions : PulseRoute("transactions")
    data object AddExpense : PulseRoute("add_expense")
    data object Recurring : PulseRoute("recurring")
    data object Settings : PulseRoute("settings")
}
