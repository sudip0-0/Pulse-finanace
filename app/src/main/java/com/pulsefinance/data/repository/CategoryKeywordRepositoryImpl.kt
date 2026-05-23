package com.pulsefinance.data.repository

import com.pulsefinance.data.local.dao.CategoryKeywordDao
import com.pulsefinance.data.mapper.toDomain
import com.pulsefinance.domain.model.CategoryKeyword
import com.pulsefinance.domain.repository.CategoryKeywordRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CategoryKeywordRepositoryImpl(
    private val categoryKeywordDao: CategoryKeywordDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CategoryKeywordRepository {

    override suspend fun getKeywords(): List<CategoryKeyword> = withContext(ioDispatcher) {
        categoryKeywordDao.observeKeywords().first().map { it.toDomain() }
    }
}
