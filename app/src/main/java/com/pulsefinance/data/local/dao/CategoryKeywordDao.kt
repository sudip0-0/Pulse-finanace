package com.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefinance.data.local.entity.CategoryKeywordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryKeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keyword: CategoryKeywordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keywords: List<CategoryKeywordEntity>)

    @Update
    suspend fun update(keyword: CategoryKeywordEntity)

    @Query("DELETE FROM category_keywords WHERE id = :keywordId")
    suspend fun deleteById(keywordId: Long)

    @Query("SELECT * FROM category_keywords ORDER BY weight DESC, keyword ASC")
    fun observeKeywords(): Flow<List<CategoryKeywordEntity>>

    @Query("SELECT * FROM category_keywords WHERE keyword = :keyword LIMIT 1")
    suspend fun getKeyword(keyword: String): CategoryKeywordEntity?

    @Query(
        """
        SELECT * FROM category_keywords
        WHERE :normalizedText LIKE '%' || keyword || '%'
        ORDER BY weight DESC, keyword ASC
        """,
    )
    suspend fun findKeywordMatches(normalizedText: String): List<CategoryKeywordEntity>
}
