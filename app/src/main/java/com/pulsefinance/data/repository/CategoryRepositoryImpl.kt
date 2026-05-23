package com.pulsefinance.data.repository

import com.pulsefinance.data.local.dao.CategoryDao
import com.pulsefinance.data.local.entity.CategoryEntity
import com.pulsefinance.data.mapper.toDomain
import com.pulsefinance.data.mapper.toEntity
import com.pulsefinance.domain.model.Category
import com.pulsefinance.domain.repository.CategoryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CategoryRepository {

    override fun observeCategories(): Flow<List<Category>> =
        categoryDao.observeActiveCategories()
            .map { entities -> entities.map { it.toDomain() } }
            .flowOn(ioDispatcher)

    override suspend fun getCategory(categoryId: Long): Category? = withContext(ioDispatcher) {
        categoryDao.getCategoryById(categoryId)?.toDomain()
    }

    override suspend fun getCategoryByName(name: String): Category? = withContext(ioDispatcher) {
        categoryDao.getCategoryByName(name)?.toDomain()
    }

    override suspend fun addCustomCategory(name: String, colorHex: String): Long = withContext(ioDispatcher) {
        val id = categoryDao.getMaxCategoryId() + 1
        categoryDao.insert(
            CategoryEntity(
                id = id,
                name = name.trim(),
                iconKey = "category",
                colorHex = colorHex,
                sortOrder = categoryDao.getMaxSortOrder() + 10,
                isDefault = false,
                isArchived = false,
            ),
        )
    }

    override suspend fun updateCategory(category: Category) = withContext(ioDispatcher) {
        categoryDao.update(category.toEntity())
    }

    override suspend fun archiveCustomCategory(categoryId: Long) = withContext(ioDispatcher) {
        val category = categoryDao.getCategoryById(categoryId) ?: return@withContext
        if (!category.isDefault) {
            categoryDao.update(category.copy(isArchived = true))
        }
    }
}
