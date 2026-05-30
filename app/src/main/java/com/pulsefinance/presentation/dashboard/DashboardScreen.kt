package com.pulsefinance.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.domain.model.BudgetStatus
import com.pulsefinance.presentation.common.components.PulseCard
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@Composable
fun DashboardScreen(
    onAddExpense: () -> Unit,
    onQuickAdd: (merchant: String?, category: String?) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        state.isLoading -> DashboardLoading()
        state.errorMessage != null -> DashboardError(message = state.errorMessage!!)
        state.isEmpty -> DashboardEmpty(onAddExpense = onAddExpense)
        else -> DashboardContent(
            state = state,
            onAddExpense = onAddExpense,
            onQuickAdd = onQuickAdd,
            onSearchClick = onSearchClick,
        )
    }
}

@Composable
private fun DashboardLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = PulseColors.Primary)
    }
}

@Composable
private fun DashboardError(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(PulseSpacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = PulseColors.Danger,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun DashboardEmpty(onAddExpense: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(PulseSpacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No expenses this month",
                color = PulseColors.TextSecondary,
                style = MaterialTheme.typography.titleLarge,
            )
            Button(
                onClick = onAddExpense,
                modifier = Modifier.padding(top = PulseSpacing.lg),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = "Add expense")
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    onAddExpense: () -> Unit,
    onQuickAdd: (merchant: String?, category: String?) -> Unit,
    onSearchClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background),
        contentPadding = PaddingValues(
            start = PulseSpacing.xl,
            end = PulseSpacing.xl,
            top = PulseSpacing.xl,
            bottom = PulseSpacing.xl + PulseSpacing.navHeight,
        ),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.xl),
    ) {
        item { DashboardHeader(onSearchClick = onSearchClick) }
        item { MonthlySpendCard(amount = state.monthlySpend, onAddExpense = onAddExpense) }
        if (state.budgetState != null) {
            item { BudgetCard(budget = state.budgetState) }
        }
        if (state.categorySpending.isNotEmpty()) {
            items(state.categorySpending, key = { "cat_${it.categoryId}" }) { category ->
                CategorySpendRow(
                    name = category.name,
                    amount = category.amount,
                    percent = category.percent,
                    color = parseColor(category.colorHex),
                )
            }
        }
        item { QuickAddRow(items = state.quickAddItems, onQuickAdd = onQuickAdd) }
        if (state.recentTransactions.isNotEmpty()) {
            item {
                Text(text = "Recent transactions", style = MaterialTheme.typography.titleLarge)
            }
            items(state.recentTransactions, key = { "txn_${it.id}" }) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}

@Composable
private fun DashboardHeader(onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PulseColors.SurfaceHigh)
                .semantics { contentDescription = "Profile avatar" },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "AS", style = MaterialTheme.typography.titleMedium)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = PulseSpacing.md),
        ) {
            Text(text = "Welcome,", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Sudip Shrestha", style = MaterialTheme.typography.titleLarge)
        }
        IconButton(onClick = onSearchClick) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }
    }
}

@Composable
private fun MonthlySpendCard(amount: String, onAddExpense: () -> Unit) {
    PulseCard(containerColor = PulseColors.SurfaceHigh) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "This month", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = amount,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.semantics { contentDescription = "Monthly spend: $amount" },
                )
            }
            Button(onClick = onAddExpense) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = "Add")
            }
        }
    }
}

@Composable
private fun BudgetCard(budget: BudgetUiState) {
    val progressColor = when (budget.status) {
        BudgetStatus.Under -> PulseColors.Success
        BudgetStatus.Warning -> PulseColors.Warning
        BudgetStatus.Danger -> PulseColors.Danger
        BudgetStatus.OverBudget -> PulseColors.Danger
    }
    val progressDescription = "Budget ${budget.percentLabel} used. ${budget.remainingLabel}"

    PulseCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Budget", style = MaterialTheme.typography.titleLarge)
            Text(text = budget.percentLabel, color = progressColor, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = budget.remainingLabel,
            color = PulseColors.TextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = PulseSpacing.xs),
        )
        LinearProgressIndicator(
            progress = { budget.percent },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PulseSpacing.md)
                .semantics { contentDescription = progressDescription },
            color = progressColor,
            trackColor = PulseColors.SurfacePressed,
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = budget.progressLabel,
            color = PulseColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = PulseSpacing.sm),
        )
    }
}

@Composable
private fun CategorySpendRow(
    name: String,
    amount: String,
    percent: String,
    color: Color,
) {
    PulseCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {
                    contentDescription = "$name: $amount, $percent of total"
                },
            horizontalArrangement = Arrangement.spacedBy(PulseSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(color),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium)
                Text(text = percent, color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = amount, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun TransactionItem(transaction: TransactionUiModel) {
    val accentColor = if (transaction.colorHex.isNotBlank()) parseColor(transaction.colorHex) else PulseColors.Primary
    val subtitle = listOfNotNull(
        transaction.category.takeIf { it.isNotBlank() },
        transaction.dateLabel,
    ).joinToString(" · ")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "${transaction.merchant}, ${transaction.category}, ${transaction.amount}, ${transaction.dateLabel}"
            },
        horizontalArrangement = Arrangement.spacedBy(PulseSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = transaction.merchant.first().uppercase(),
                color = accentColor,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = transaction.merchant, style = MaterialTheme.typography.titleMedium)
            Text(
                text = subtitle,
                color = PulseColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(text = transaction.amount, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun QuickAddRow(
    items: List<String>,
    onQuickAdd: (merchant: String?, category: String?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(PulseSpacing.sm)) {
        Text(text = "Quick add", style = MaterialTheme.typography.titleLarge)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
        ) {
            items(items) { label ->
                AssistChip(
                    onClick = {
                        val prefill = quickAddPrefill(label)
                        onQuickAdd(prefill.merchant, prefill.category)
                    },
                    label = { Text(text = label) },
                )
            }
        }
    }
}

private data class QuickAddPrefill(val merchant: String?, val category: String?)

private fun quickAddPrefill(label: String): QuickAddPrefill = when (label) {
    "Food" -> QuickAddPrefill(merchant = null, category = "Food & Dining")
    "Pathao" -> QuickAddPrefill(merchant = "Pathao", category = "Transport")
    "Daraz" -> QuickAddPrefill(merchant = "Daraz", category = "Shopping")
    "Fuel" -> QuickAddPrefill(merchant = null, category = "Fuel")
    "NTC/Ncell" -> QuickAddPrefill(merchant = "NTC", category = "Mobile Recharge")
    "eSewa/Khalti" -> QuickAddPrefill(merchant = "eSewa", category = "Wallet & Transfers")
    else -> QuickAddPrefill(merchant = null, category = "Other")
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        PulseColors.Other
    }
}
