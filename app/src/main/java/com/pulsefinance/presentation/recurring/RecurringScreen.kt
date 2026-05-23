package com.pulsefinance.presentation.recurring

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    onAddRule: () -> Unit = {},
    onEditRule: (Long) -> Unit = {},
    viewModel: RecurringViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var deleteConfirmRuleId by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring Expenses") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseColors.Background),
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRule,
                containerColor = PulseColors.Primary,
                contentColor = PulseColors.TextPrimary,
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add recurring rule")
            }
        },
        containerColor = PulseColors.Background,
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "Loading...", color = PulseColors.TextSecondary)
                }
            }
            state.isEmpty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No recurring expenses yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = PulseColors.TextSecondary,
                        )
                        Spacer(modifier = Modifier.height(PulseSpacing.xs))
                        Text(
                            text = "Add rent, internet, electricity, or other regular bills",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PulseColors.TextMuted,
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = PulseSpacing.xl),
                    verticalArrangement = Arrangement.spacedBy(PulseSpacing.sm),
                ) {
                    items(state.rules, key = { it.id }) { rule ->
                        RecurringRuleCard(
                            rule = rule,
                            onTogglePause = { viewModel.onTogglePause(rule.id) },
                            onDelete = { deleteConfirmRuleId = rule.id },
                            onClick = { onEditRule(rule.id) },
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (deleteConfirmRuleId != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmRuleId = null },
            title = { Text("Delete recurring rule?") },
            text = { Text("This will not remove previously generated expenses.") },
            confirmButton = {
                TextButton(onClick = {
                    deleteConfirmRuleId?.let { viewModel.onDeleteRule(it) }
                    deleteConfirmRuleId = null
                }) { Text("Delete", color = PulseColors.Danger) }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmRuleId = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun RecurringRuleCard(
    rule: RecurringRuleUiModel,
    onTogglePause: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
) {
    val alpha = if (rule.isActive) 1f else 0.5f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PulseColors.Surface)
            .clickable(onClick = onClick)
            .padding(PulseSpacing.lg)
            .semantics {
                contentDescription = "${rule.title}, ${rule.amount}, ${rule.frequencyLabel}, ${rule.nextDueDateLabel}"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Category color dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(parseColor(rule.categoryColorHex).copy(alpha = alpha)),
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = PulseSpacing.sm),
        ) {
            Text(
                text = rule.title,
                style = MaterialTheme.typography.titleMedium,
                color = PulseColors.TextPrimary.copy(alpha = alpha),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${rule.frequencyLabel} · ${rule.amount}",
                style = MaterialTheme.typography.bodyMedium,
                color = PulseColors.TextSecondary.copy(alpha = alpha),
            )
            Text(
                text = if (rule.isActive) rule.nextDueDateLabel else "Paused",
                style = MaterialTheme.typography.bodySmall,
                color = if (!rule.isActive) PulseColors.Warning
                else if (rule.nextDueDateLabel == "Overdue") PulseColors.Danger
                else PulseColors.TextMuted,
            )
        }

        IconButton(onClick = onTogglePause) {
            Icon(
                imageVector = if (rule.isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (rule.isActive) "Pause" else "Resume",
                tint = PulseColors.TextSecondary,
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = PulseColors.Danger.copy(alpha = 0.7f),
            )
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
