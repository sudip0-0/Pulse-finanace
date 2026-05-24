package com.pulsefinance.presentation.analytics

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background),
    ) {
        TopAppBar(
            title = { Text(text = "Analytics") },
            actions = { PeriodPicker(currentLabel = state.periodLabel, onPeriodSelected = viewModel::onPeriodSelected) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseColors.Background),
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PulseColors.Primary)
            }
        } else if (state.categoryBreakdown.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(PulseSpacing.xl),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Add expenses to see insights",
                    color = PulseColors.TextSecondary,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        } else {
            AnalyticsContent(state = state)
        }
    }
}

@Composable
private fun AnalyticsContent(state: AnalyticsUiState) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = PulseSpacing.xl,
            end = PulseSpacing.xl,
            bottom = PulseSpacing.xl + PulseSpacing.navHeight,
        ),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.lg),
    ) {
        // Donut chart
        item {
            DonutChart(
                breakdown = state.categoryBreakdown,
                totalSpend = state.totalSpend,
                accessibilitySummary = state.chartAccessibilitySummary,
            )
        }

        // Legend
        items(state.categoryBreakdown, key = { "cat_${it.categoryId}" }) { item ->
            LegendRow(item = item)
        }

        // Recent transactions
        if (state.recentTransactions.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(PulseSpacing.sm))
                Text(text = "Recent transactions", style = MaterialTheme.typography.titleLarge)
            }
            items(state.recentTransactions, key = { "txn_${it.id}" }) { transaction ->
                RecentTransactionRow(transaction = transaction)
            }
        }
    }
}

@Composable
private fun DonutChart(
    breakdown: List<CategoryBreakdownItem>,
    totalSpend: String,
    accessibilitySummary: String,
) {
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(breakdown) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 600))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = accessibilitySummary },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(
                modifier = Modifier
                    .size(220.dp)
                    .padding(PulseSpacing.md),
            ) {
                val strokeWidth = 32.dp.toPx()
                val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)
                val gap = 4f
                var startAngle = -90f

                breakdown.forEach { item ->
                    val sweep = item.sweepAngle * animationProgress.value
                    if (sweep > 0.5f) {
                        drawArc(
                            color = parseColor(item.colorHex),
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = stroke,
                        )
                    }
                    startAngle += sweep + gap
                }
            }
            // Center text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = totalSpend, style = MaterialTheme.typography.headlineMedium)
                Text(text = "Total spend", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun LegendRow(item: CategoryBreakdownItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "${item.name}: ${item.amount}, ${item.percent}%"
            },
        horizontalArrangement = Arrangement.spacedBy(PulseSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(parseColor(item.colorHex)),
        )
        Text(text = item.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Text(text = item.amount, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "${item.percent}%",
            color = PulseColors.TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = PulseSpacing.xs),
        )
    }
}

@Composable
private fun RecentTransactionRow(transaction: AnalyticsTransactionItem) {
    val accentColor = if (transaction.categoryColorHex.isNotBlank()) parseColor(transaction.categoryColorHex) else PulseColors.Primary
    val subtitle = listOfNotNull(
        transaction.categoryName.takeIf { it.isNotBlank() },
        transaction.dateLabel,
    ).joinToString(" · ")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "${transaction.merchant}, ${transaction.categoryName}, ${transaction.amount}, ${transaction.dateLabel}"
            },
        horizontalArrangement = Arrangement.spacedBy(PulseSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
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
            Text(text = subtitle, color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
        Text(text = transaction.amount, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun PeriodPicker(currentLabel: String, onPeriodSelected: (AnalyticsPeriod) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = currentLabel, color = PulseColors.TextSecondary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AnalyticsPeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.label) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        PulseColors.Other
    }
}
