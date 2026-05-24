package com.pulsefinance.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulsefinance.domain.model.Category
import com.pulsefinance.presentation.common.components.PulseCard
import com.pulsefinance.presentation.common.theme.PulseColors
import com.pulsefinance.presentation.common.theme.PulseSpacing

private val CategoryColors = listOf(
    "#2F80FF",
    "#35C76B",
    "#FFB020",
    "#E94F86",
    "#7C5CFF",
    "#2DD4BF",
    "#F87171",
    "#94A3B8",
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onBack: () -> Unit,
    viewModel: CategoriesViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background),
    ) {
        TopAppBar(
            title = { Text("Categories") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = viewModel::onAddCategoryClick) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add category")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = PulseColors.Background),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = PulseSpacing.xl,
                end = PulseSpacing.xl,
                top = PulseSpacing.sm,
                bottom = PulseSpacing.xl,
            ),
            verticalArrangement = Arrangement.spacedBy(PulseSpacing.sm),
        ) {
            if (state.errorMessage != null && !state.showEditor) {
                item {
                    Text(
                        text = state.errorMessage!!,
                        color = PulseColors.Danger,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            items(state.categories, key = { it.id }) { category ->
                CategoryRow(
                    category = category,
                    onEdit = { viewModel.onEditCategoryClick(category) },
                    onArchive = { viewModel.onArchiveCategory(category) },
                )
            }
        }
    }

    if (state.showEditor) {
        AlertDialog(
            onDismissRequest = viewModel::onEditorDismissed,
            title = { Text(if (state.isEditing) "Edit category" else "Add category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(PulseSpacing.md)) {
                    OutlinedTextField(
                        value = state.editorName,
                        onValueChange = viewModel::onEditorNameChanged,
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PulseColors.Primary,
                            unfocusedBorderColor = PulseColors.SurfaceHigh,
                            focusedLabelColor = PulseColors.Primary,
                            unfocusedLabelColor = PulseColors.TextSecondary,
                            cursorColor = PulseColors.Primary,
                        ),
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
                        verticalArrangement = Arrangement.spacedBy(PulseSpacing.xs),
                    ) {
                        CategoryColors.forEach { colorHex ->
                            FilterChip(
                                selected = state.editorColorHex == colorHex,
                                onClick = { viewModel.onEditorColorSelected(colorHex) },
                                label = {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(parseColor(colorHex)),
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PulseColors.SurfaceHigh,
                                    containerColor = PulseColors.Surface,
                                ),
                            )
                        }
                    }
                    if (state.errorMessage != null) {
                        Text(
                            text = state.errorMessage!!,
                            color = PulseColors.Danger,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::onSaveCategory) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onEditorDismissed) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    onEdit: () -> Unit,
    onArchive: () -> Unit,
) {
    PulseCard(
        modifier = if (category.isDefault) Modifier else Modifier.clickable(onClick = onEdit),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PulseSpacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(parseColor(category.colorHex).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(parseColor(category.colorHex)),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (category.isDefault) "Default category" else "Custom category",
                    color = PulseColors.TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (!category.isDefault) {
                IconButton(onClick = onArchive) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Archive ${category.name}")
                }
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
