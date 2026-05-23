package com.pulsefinance.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pulsefinance.presentation.analytics.AnalyticsScreen
import com.pulsefinance.presentation.common.components.PulseBottomBar
import com.pulsefinance.presentation.dashboard.DashboardScreen
import com.pulsefinance.presentation.expense.AddExpenseScreen
import com.pulsefinance.presentation.recurring.RecurringScreen
import com.pulsefinance.presentation.settings.SettingsScreen
import com.pulsefinance.presentation.transactions.TransactionsScreen

@Composable
fun PulseNavGraph() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            PulseBottomBar(
                selectedRoute = currentRoute,
                onRouteSelected = { route ->
                    navController.navigate(route.path) {
                        popUpTo(PulseRoute.Dashboard.path) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PulseRoute.Dashboard.path,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(PulseRoute.Dashboard.path) {
                DashboardScreen(onAddExpense = { navController.navigate(PulseRoute.AddExpense.path) })
            }
            composable(PulseRoute.Analytics.path) {
                AnalyticsScreen()
            }
            composable(PulseRoute.Transactions.path) {
                TransactionsScreen()
            }
            composable(PulseRoute.AddExpense.path) {
                AddExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable(PulseRoute.Recurring.path) {
                RecurringScreen()
            }
            composable(PulseRoute.Settings.path) {
                SettingsScreen(onRecurringClick = { navController.navigate(PulseRoute.Recurring.path) })
            }
        }
    }
}
