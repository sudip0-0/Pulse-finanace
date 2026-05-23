package com.pulsefinance.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pulsefinance.presentation.analytics.AnalyticsScreen
import com.pulsefinance.presentation.common.components.PulseBottomBar
import com.pulsefinance.presentation.dashboard.DashboardScreen
import com.pulsefinance.presentation.expense.AddExpenseScreen
import com.pulsefinance.presentation.recurring.AddRecurringRuleScreen
import com.pulsefinance.presentation.recurring.RecurringScreen
import com.pulsefinance.presentation.settings.CategoriesScreen
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
                DashboardScreen(
                    onAddExpense = { navController.navigate(PulseRoute.AddExpense.path) },
                    onQuickAdd = { merchant, category ->
                        navController.navigate(PulseRoute.AddExpensePrefilled.withPrefill(merchant, category))
                    },
                    onSearchClick = { navController.navigate(PulseRoute.Transactions.path) },
                )
            }
            composable(PulseRoute.Analytics.path) {
                AnalyticsScreen()
            }
            composable(PulseRoute.Transactions.path) {
                TransactionsScreen(
                    onEditExpense = { expenseId ->
                        navController.navigate(PulseRoute.EditExpense.withId(expenseId))
                    },
                )
            }
            composable(PulseRoute.AddExpense.path) {
                AddExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = PulseRoute.AddExpensePrefilled.path,
                arguments = listOf(
                    navArgument("merchant") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("category") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                ),
            ) {
                AddExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = PulseRoute.EditExpense.path,
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType }),
            ) {
                AddExpenseScreen(onBack = { navController.popBackStack() })
            }
            composable(PulseRoute.Recurring.path) {
                RecurringScreen(
                    onAddRule = { navController.navigate(PulseRoute.AddRecurringRule.path) },
                    onEditRule = { ruleId ->
                        navController.navigate(PulseRoute.EditRecurringRule.withId(ruleId))
                    },
                )
            }
            composable(PulseRoute.AddRecurringRule.path) {
                AddRecurringRuleScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = PulseRoute.EditRecurringRule.path,
                arguments = listOf(navArgument("ruleId") { type = NavType.LongType }),
            ) {
                AddRecurringRuleScreen(onBack = { navController.popBackStack() })
            }
            composable(PulseRoute.Settings.path) {
                SettingsScreen(
                    onRecurringClick = { navController.navigate(PulseRoute.Recurring.path) },
                    onCategoriesClick = { navController.navigate(PulseRoute.Categories.path) },
                )
            }
            composable(PulseRoute.Categories.path) {
                CategoriesScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
