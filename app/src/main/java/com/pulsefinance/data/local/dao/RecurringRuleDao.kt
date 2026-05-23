package com.pulsefinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.pulsefinance.data.local.entity.RecurringRuleEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringRuleDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rule: RecurringRuleEntity): Long

    @Update
    suspend fun update(rule: RecurringRuleEntity)

    @Delete
    suspend fun delete(rule: RecurringRuleEntity)

    @Query("DELETE FROM recurring_rules WHERE id = :ruleId")
    suspend fun deleteById(ruleId: Long)

    @Query("SELECT * FROM recurring_rules WHERE id = :ruleId LIMIT 1")
    suspend fun getRuleById(ruleId: Long): RecurringRuleEntity?

    @Query("SELECT * FROM recurring_rules ORDER BY next_due_date ASC, title ASC")
    fun observeRules(): Flow<List<RecurringRuleEntity>>

    @Query(
        """
        SELECT * FROM recurring_rules
        WHERE is_active = 1 AND next_due_date <= :date
        ORDER BY next_due_date ASC, title ASC
        """,
    )
    suspend fun getActiveRulesDueOnOrBefore(date: LocalDate): List<RecurringRuleEntity>

    @Query(
        """
        SELECT * FROM recurring_rules
        WHERE is_active = 1
        ORDER BY next_due_date ASC, title ASC
        """,
    )
    fun observeActiveRules(): Flow<List<RecurringRuleEntity>>
}
