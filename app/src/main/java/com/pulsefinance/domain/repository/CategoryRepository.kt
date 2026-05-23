package com.pulsefinance.domain.repository

import com.pulsefinance.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun getCategory(categoryId: Long): Category?
    suspend fun getCategoryByName(name: String): Category?
    suspend fun addCustomCategory(name: String, colorHex: String): Long
    suspend fun updateCategory(category: Category)
    suspend fun archiveCustomCategory(categoryId: Long)
}
