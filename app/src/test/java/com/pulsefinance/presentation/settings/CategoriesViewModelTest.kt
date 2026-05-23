package com.pulsefinance.presentation.settings

import com.pulsefinance.domain.FakeCategoryRepository
import com.pulsefinance.domain.category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CategoriesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads categories on init`() = runTest {
        val viewModel = CategoriesViewModel(
            FakeCategoryRepository(listOf(category(1, "Food & Dining"), category(3, "Transport"))),
        )
        advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.categories.size)
    }

    @Test
    fun `adds custom category`() = runTest {
        val viewModel = CategoriesViewModel(FakeCategoryRepository(listOf(category(1, "Food & Dining"))))
        advanceUntilIdle()

        viewModel.onAddCategoryClick()
        viewModel.onEditorNameChanged("Pets")
        viewModel.onEditorColorSelected("#F87171")
        viewModel.onSaveCategory()
        advanceUntilIdle()

        val saved = viewModel.uiState.value.categories.first { it.name == "Pets" }
        assertFalse(saved.isDefault)
        assertEquals("#F87171", saved.colorHex)
        assertFalse(viewModel.uiState.value.showEditor)
    }

    @Test
    fun `updates custom category`() = runTest {
        val custom = category(20, "Pets").copy(isDefault = false, colorHex = "#2F80FF")
        val viewModel = CategoriesViewModel(FakeCategoryRepository(listOf(custom)))
        advanceUntilIdle()

        viewModel.onEditCategoryClick(custom)
        viewModel.onEditorNameChanged("Family")
        viewModel.onEditorColorSelected("#35C76B")
        viewModel.onSaveCategory()
        advanceUntilIdle()

        val saved = viewModel.uiState.value.categories.first()
        assertEquals("Family", saved.name)
        assertEquals("#35C76B", saved.colorHex)
    }

    @Test
    fun `does not edit default category`() = runTest {
        val defaultCategory = category(1, "Food & Dining")
        val viewModel = CategoriesViewModel(FakeCategoryRepository(listOf(defaultCategory)))
        advanceUntilIdle()

        viewModel.onEditCategoryClick(defaultCategory)

        assertFalse(viewModel.uiState.value.showEditor)
    }

    @Test
    fun `archives custom category`() = runTest {
        val custom = category(20, "Pets").copy(isDefault = false)
        val viewModel = CategoriesViewModel(FakeCategoryRepository(listOf(custom)))
        advanceUntilIdle()

        viewModel.onArchiveCategory(custom)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.categories.none { it.id == custom.id })
    }

    @Test
    fun `rejects duplicate category names`() = runTest {
        val viewModel = CategoriesViewModel(FakeCategoryRepository(listOf(category(1, "Food & Dining"))))
        advanceUntilIdle()

        viewModel.onAddCategoryClick()
        viewModel.onEditorNameChanged("Food & Dining")
        viewModel.onSaveCategory()
        advanceUntilIdle()

        assertEquals("Category name already exists.", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.showEditor)
    }
}
