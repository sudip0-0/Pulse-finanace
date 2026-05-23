package com.pulsefinance.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.pulsefinance.presentation.common.components.CategorySpendCard
import com.pulsefinance.presentation.common.components.PulseCard
import com.pulsefinance.presentation.common.components.TransactionRow
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing
import com.pulsefinance.presentation.preview.PulsePreviewData

@Composable
fun DashboardScreen(onAddExpense: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(horizontal = PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.xl),
    ) {
        item { DashboardHeader() }
        item { MonthlySpendCard(onAddExpense = onAddExpense) }
        item { BudgetCard() }
        items(PulsePreviewData.categories.take(3)) { category ->
            CategorySpendCard(
                name = category.name,
                amount = category.amount,
                percent = category.percent,
                color = category.color,
            )
        }
        item { QuickAddRow() }
        item {
            Text(text = "Recent transactions", style = MaterialTheme.typography.titleLarge)
        }
        items(PulsePreviewData.transactions.take(4)) { transaction ->
            TransactionRow(
                merchant = transaction.merchant,
                category = transaction.category,
                amount = transaction.amount,
                dateLabel = transaction.dateLabel,
                color = transaction.color,
            )
        }
    }
}

@Composable
private fun DashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = PulseSpacing.xl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PulseColors.SurfaceHigh),
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
            Text(text = "Aayush Shrestha", style = MaterialTheme.typography.titleLarge)
        }
        IconButton(onClick = {}, enabled = false) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(onClick = {}, enabled = false) {
            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
        }
    }
}

@Composable
private fun MonthlySpendCard(onAddExpense: () -> Unit) {
    PulseCard(containerColor = PulseColors.SurfaceHigh) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "This month", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
                Text(text = "NPR 2,418.50", style = MaterialTheme.typography.displayMedium)
            }
            Button(onClick = onAddExpense) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = "Add expense")
            }
        }
    }
}

@Composable
private fun BudgetCard() {
    PulseCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "Budget", style = MaterialTheme.typography.titleLarge)
            Text(text = "69%", color = PulseColors.Success, style = MaterialTheme.typography.titleMedium)
        }
        Text(
            text = "NPR 1,081.50 left",
            color = PulseColors.TextSecondary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = PulseSpacing.xs),
        )
        LinearProgressIndicator(
            progress = { 0.69f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = PulseSpacing.md),
            color = PulseColors.Success,
            trackColor = PulseColors.SurfacePressed,
            strokeCap = StrokeCap.Round,
        )
        Text(
            text = "NPR 2,418.50 of NPR 3,500.00",
            color = PulseColors.TextMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = PulseSpacing.sm),
        )
    }
}

@Composable
private fun QuickAddRow() {
    Column(verticalArrangement = Arrangement.spacedBy(PulseSpacing.sm)) {
        Text(text = "Quick add", style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
        ) {
            PulsePreviewData.quickAddMerchants.take(4).forEach { label ->
                AssistChip(onClick = {}, label = { Text(text = label) }, enabled = false)
            }
        }
    }
}
