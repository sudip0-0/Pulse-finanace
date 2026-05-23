package com.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
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

    @Delete
    suspend fun delete(expense: ExpenseEntity)

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
        WHERE expense_date BETWEEN :startDate AND :endDate
        ORDER BY expense_date DESC, created_at DESC
        """,
    )
    suspend fun getExpensesBetween(startDate: LocalDate, endDate: LocalDate): List<ExpenseEntity>

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
    suspend fun getCategorySpendingBetween(
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<CategorySpendRow>
}
