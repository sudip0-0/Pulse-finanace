package com.pulsefinance.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState

    init {
        viewModelScope.launch {
            categoryRepository.observeCategories()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Failed to load categories.")
                }
                .collect { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories)
                }
        }
    }

    fun onAddCategoryClick() {
        _uiState.value = _uiState.value.copy(
            showEditor = true,
            editingCategoryId = null,
            editorName = "",
            editorColorHex = CategoriesUiState.DEFAULT_CATEGORY_COLOR,
            errorMessage = null,
        )
    }

    fun onEditCategoryClick(category: Category) {
        if (category.isDefault) return
        _uiState.value = _uiState.value.copy(
            showEditor = true,
            editingCategoryId = category.id,
            editorName = category.name,
            editorColorHex = category.colorHex,
            errorMessage = null,
        )
    }

    fun onEditorNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(editorName = name, errorMessage = null)
    }

    fun onEditorColorSelected(colorHex: String) {
        _uiState.value = _uiState.value.copy(editorColorHex = colorHex, errorMessage = null)
    }

    fun onEditorDismissed() {
        _uiState.value = _uiState.value.copy(showEditor = false, errorMessage = null)
    }

    fun onSaveCategory() {
        val state = _uiState.value
        val name = state.editorName.trim()
        if (name.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Category name is required.")
            return
        }

        viewModelScope.launch {
            try {
                val existing = categoryRepository.getCategoryByName(name)
                val editingId = state.editingCategoryId
                if (existing != null && existing.id != editingId) {
                    _uiState.value = _uiState.value.copy(errorMessage = "Category name already exists.")
                    return@launch
                }

                if (editingId == null) {
                    categoryRepository.addCustomCategory(name, state.editorColorHex)
                } else {
                    val current = categoryRepository.getCategory(editingId)
                    if (current == null || current.isDefault) {
                        _uiState.value = _uiState.value.copy(errorMessage = "Default categories cannot be edited.")
                        return@launch
                    }
                    categoryRepository.updateCategory(
                        current.copy(name = name, colorHex = state.editorColorHex),
                    )
                }
                _uiState.value = _uiState.value.copy(showEditor = false, errorMessage = null)
            } catch (error: Throwable) {
                _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Failed to save category.")
            }
        }
    }

    fun onArchiveCategory(category: Category) {
        if (category.isDefault) return
        viewModelScope.launch {
            try {
                categoryRepository.archiveCustomCategory(category.id)
            } catch (error: Throwable) {
                _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Failed to archive category.")
            }
        }
    }
}
