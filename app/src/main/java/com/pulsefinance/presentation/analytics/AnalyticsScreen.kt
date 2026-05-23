package com.pulsefinance.presentation.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.pulsefinance.presentation.common.components.TransactionRow
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing
import com.pulsefinance.presentation.preview.PulsePreviewData

@Composable
fun AnalyticsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(horizontal = PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.xl),
    ) {
        item {
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = PulseSpacing.xl),
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(PulseSpacing.sm)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Spending") }, enabled = false)
                FilterChip(selected = false, onClick = {}, label = { Text("Budget") }, enabled = false)
            }
        }
        item { DonutPreview() }
        items(PulsePreviewData.categories) { category ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = category.name, color = category.color, style = MaterialTheme.typography.titleMedium)
                Text(text = "${category.amount}  ${category.percent}", style = MaterialTheme.typography.bodyLarge)
            }
        }
        item { Text(text = "Recent transactions", style = MaterialTheme.typography.titleLarge) }
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
private fun DonutPreview() {
    Column {
        Canvas(
            modifier = Modifier
                .size(220.dp)
                .padding(PulseSpacing.md),
        ) {
            val stroke = Stroke(width = 34.dp.toPx(), cap = StrokeCap.Round)
            val diameter = size.minDimension - stroke.width
            val topLeft = Offset(stroke.width / 2, stroke.width / 2)
            val arcSize = Size(diameter, diameter)
            var startAngle = -90f
            val sweeps = listOf(100f, 76f, 86f, 58f)
            val colors = PulsePreviewData.categories.map { it.color }
            sweeps.forEachIndexed { index, sweep ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = stroke,
                )
                startAngle += sweep + 8f
            }
        }
        Text(text = "NPR 1,738.00", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Total spend", color = PulseColors.TextSecondary, style = MaterialTheme.typography.bodyLarge)
    }
}
