package com.pulsefinance.domain.repository

import com.pulsefinance.domain.model.CategoryKeyword

interface CategoryKeywordRepository {
    suspend fun getKeywords(): List<CategoryKeyword>
}
