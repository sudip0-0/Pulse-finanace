package com.pulsefinance.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.pulsefinance.presentation.common.components.TransactionRow
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing
import com.pulsefinance.presentation.preview.PulsePreviewData

@Composable
fun TransactionsScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(horizontal = PulseSpacing.xl),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.xl),
    ) {
        item {
            Text(
                text = "Transactions",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = PulseSpacing.xl),
            )
        }
        items(PulsePreviewData.transactions) { transaction ->
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
