package com.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefinance.data.local.entity.BudgetEntity
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE month = :month LIMIT 1")
    fun observeBudgetForMonth(month: YearMonth): Flow<BudgetEntity?>

    @Query("SELECT * FROM budgets WHERE month = :month LIMIT 1")
    suspend fun getBudgetForMonth(month: YearMonth): BudgetEntity?

    @Query("SELECT * FROM budgets ORDER BY month DESC")
    fun observeBudgets(): Flow<List<BudgetEntity>>
}
