package com.pulsefinance.presentation.settings

import com.pulsefinance.domain.model.Category

data class CategoriesUiState(
    val categories: List<Category> = emptyList(),
    val showEditor: Boolean = false,
    val editingCategoryId: Long? = null,
    val editorName: String = "",
    val editorColorHex: String = DEFAULT_CATEGORY_COLOR,
    val errorMessage: String? = null,
) {
    val isEditing: Boolean = editingCategoryId != null

    companion object {
        const val DEFAULT_CATEGORY_COLOR = "#2F80FF"
    }
}
