package com.pulsefinance.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    onEditExpense: (Long) -> Unit = {},
    viewModel: TransactionsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background),
    ) {
        TopAppBar(
            title = { Text(text = "Transactions") },
            actions = {
                SortMenu(
                    currentSort = state.sortOrder,
                    onSortSelected = viewModel::onSortChanged,
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseColors.Background),
        )

        // Search bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = viewModel::onSearchChanged,
            placeholder = { Text("Search transactions...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (state.searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.onSearchChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PulseSpacing.xl),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PulseColors.Primary,
                unfocusedBorderColor = PulseColors.SurfaceHigh,
                focusedTextColor = PulseColors.TextPrimary,
                unfocusedTextColor = PulseColors.TextPrimary,
                cursorColor = PulseColors.Primary,
                focusedPlaceholderColor = PulseColors.TextMuted,
                unfocusedPlaceholderColor = PulseColors.TextMuted,
                focusedLeadingIconColor = PulseColors.TextSecondary,
                unfocusedLeadingIconColor = PulseColors.TextMuted,
            ),
        )

        // Category filter chips
        if (state.categories.isNotEmpty()) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = PulseSpacing.xl, vertical = PulseSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
            ) {
                items(state.categories) { category ->
                    val selected = state.selectedCategoryId == category.id
                    FilterChip(
                        selected = selected,
                        onClick = { viewModel.onCategoryFilterSelected(category.id) },
                        label = { Text(text = category.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PulseColors.SurfaceHigh,
                            selectedLabelColor = PulseColors.TextPrimary,
                            containerColor = PulseColors.Surface,
                            labelColor = PulseColors.TextSecondary,
                        ),
                    )
                }
            }
        }

        // Content
        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PulseColors.Primary)
                }
            }
            state.isEmpty -> TransactionsEmpty(hasFilters = state.hasActiveFilters, onClearFilters = viewModel::onClearFilters)
            else -> TransactionsList(
                transactions = state.transactions,
                sortOrder = state.sortOrder,
                onEdit = onEditExpense,
                onDelete = viewModel::onDeleteRequested,
            )
        }

        // Error
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage!!,
                color = PulseColors.Danger,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(PulseSpacing.xl),
            )
        }
    }

    // Delete confirmation dialog
    state.deleteConfirmation?.let { confirmation ->
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismissed,
            title = { Text("Delete expense") },
            text = { Text("Delete \"${confirmation.title}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = viewModel::onDeleteConfirmed) {
                    Text("Delete", color = PulseColors.Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDeleteDismissed) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun TransactionsEmpty(hasFilters: Boolean, onClearFilters: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PulseSpacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (hasFilters) "No transactions found" else "No transactions yet",
                color = PulseColors.TextSecondary,
                style = MaterialTheme.typography.titleLarge,
            )
            if (hasFilters) {
                TextButton(onClick = onClearFilters, modifier = Modifier.padding(top = PulseSpacing.sm)) {
                    Text("Clear filters")
                }
            }
        }
    }
}

@Composable
private fun TransactionsList(
    transactions: List<TransactionItemUiModel>,
    sortOrder: TransactionSort,
    onEdit: (Long) -> Unit,
    onDelete: (TransactionItemUiModel) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(
            start = PulseSpacing.xl,
            end = PulseSpacing.xl,
            top = PulseSpacing.sm,
            bottom = PulseSpacing.xl + PulseSpacing.navHeight,
        ),
        verticalArrangement = Arrangement.spacedBy(PulseSpacing.sm),
    ) {
        items(transactions, key = { it.id }) { transaction ->
            TransactionListItem(
                transaction = transaction,
                onEdit = { onEdit(transaction.id) },
                onDelete = { onDelete(transaction) },
            )
        }
    }
}

@Composable
private fun TransactionListItem(
    transaction: TransactionItemUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val color = parseColor(transaction.categoryColorHex)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .background(PulseColors.Surface)
            .clickable(onClick = onEdit)
            .padding(PulseSpacing.md)
            .semantics(mergeDescendants = true) {
                contentDescription = "${transaction.merchant ?: transaction.title}, ${transaction.categoryName}, ${transaction.amount}, ${transaction.dateLabel}"
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
            Text(
                text = (transaction.merchant ?: transaction.title).first().uppercase(),
                color = color,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.merchant ?: transaction.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${transaction.categoryName} · ${transaction.dateLabel}",
                color = PulseColors.TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Text(text = transaction.amount, style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = PulseColors.TextMuted,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SortMenu(currentSort: TransactionSort, onSortSelected: (TransactionSort) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(text = sortLabel(currentSort), color = PulseColors.TextSecondary)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TransactionSort.entries.forEach { sort ->
                DropdownMenuItem(
                    text = { Text(sortLabel(sort)) },
                    onClick = {
                        onSortSelected(sort)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun sortLabel(sort: TransactionSort): String = when (sort) {
    TransactionSort.DateDesc -> "Newest first"
    TransactionSort.DateAsc -> "Oldest first"
    TransactionSort.AmountDesc -> "Highest amount"
    TransactionSort.AmountAsc -> "Lowest amount"
}

private fun parseColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        PulseColors.Other
    }
}
