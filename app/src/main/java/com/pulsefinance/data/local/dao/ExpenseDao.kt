package com.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefinance.data.local.entity.ExpenseEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(expense: ExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(expenses: List<ExpenseEntity>)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    suspend fun deleteById(expenseId: Long)

    @Query("SELECT * FROM expenses WHERE id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: Long): ExpenseEntity?

    @Query(
        """
        SELECT * FROM expenses
        WHERE expense_date BETWEEN :startDate AND :endDate
        ORDER BY expense_date DESC, created_at DESC
        """,
    )
    fun observeExpensesBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<ExpenseEntity>>

    @Query(
        """
        SELECT * FROM expenses
        ORDER BY expense_date DESC, created_at DESC
        LIMIT :limit
        """,
    )
    fun observeRecentExpenses(limit: Int): Flow<List<ExpenseEntity>>

    @Query(
        """
        SELECT
            c.id AS category_id,
            c.name AS category_name,
            c.color_hex AS color_hex,
            SUM(e.amount_minor) AS amount_minor,
            COUNT(e.id) AS transaction_count
        FROM expenses e
        INNER JOIN categories c ON c.id = e.category_id
        WHERE e.expense_date BETWEEN :startDate AND :endDate
        GROUP BY c.id, c.name, c.color_hex
        ORDER BY amount_minor DESC
        """,
    )
    fun observeCategorySpendingBetween(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<CategorySpendRow>>

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM expenses
            WHERE recurring_rule_id = :ruleId AND expense_date = :date
            LIMIT 1
        )
        """,
    )
    suspend fun hasGeneratedExpenseForRule(ruleId: Long, date: LocalDate): Boolean

    @Query(
        """
        SELECT category_id FROM expenses
        WHERE LOWER(merchant) = LOWER(:merchant)
        ORDER BY expense_date DESC, created_at DESC
        LIMIT 1
        """,
    )
    suspend fun findPreviousCategoryIdForMerchant(merchant: String): Long?

    @Query(
        """
        SELECT * FROM expenses
        WHERE (:startDate IS NULL OR expense_date >= :startDate)
          AND (:endDate IS NULL OR expense_date <= :endDate)
          AND (:categoryId IS NULL OR category_id = :categoryId)
          AND (:searchQuery IS NULL OR title LIKE '%' || :searchQuery || '%' OR merchant LIKE '%' || :searchQuery || '%')
        ORDER BY expense_date DESC, created_at DESC
        """,
    )
    fun observeFilteredExpenses(
        startDate: LocalDate?,
        endDate: LocalDate?,
        categoryId: Long?,
        searchQuery: String?,
    ): Flow<List<ExpenseEntity>>
}
