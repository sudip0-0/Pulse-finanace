package com.pulsefinance.domain.repository

import com.pulsefinance.domain.model.CategoryKeyword
import kotlinx.coroutines.flow.Flow

interface CategoryKeywordRepository {
    fun observeKeywords(): Flow<List<CategoryKeyword>>
    suspend fun getKeywords(): List<CategoryKeyword>
}
