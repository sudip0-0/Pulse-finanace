package com.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefinance.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query(
        """
        SELECT * FROM categories
        WHERE is_archived = 0
        ORDER BY sort_order ASC, name ASC
        """,
    )
    fun observeActiveCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): CategoryEntity?
}
