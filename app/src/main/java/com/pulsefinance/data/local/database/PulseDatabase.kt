package com.pulsefinance.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pulsefinance.data.local.dao.BudgetDao
import com.pulsefinance.data.local.dao.CategoryDao
import com.pulsefinance.data.local.dao.CategoryKeywordDao
import com.pulsefinance.data.local.dao.ExpenseDao
import com.pulsefinance.data.local.dao.RecurringRuleDao
import com.pulsefinance.data.local.entity.BudgetEntity
import com.pulsefinance.data.local.entity.CategoryEntity
import com.pulsefinance.data.local.entity.CategoryKeywordEntity
import com.pulsefinance.data.local.entity.ExpenseEntity
import com.pulsefinance.data.local.entity.RecurringRuleEntity

@Database(
    entities = [
        ExpenseEntity::class,
        CategoryEntity::class,
        CategoryKeywordEntity::class,
        RecurringRuleEntity::class,
        BudgetEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(PulseTypeConverters::class)
abstract class PulseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun categoryKeywordDao(): CategoryKeywordDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        const val DATABASE_NAME = "pulse.db"

        fun create(context: Context): PulseDatabase {
            return Room.databaseBuilder(context, PulseDatabase::class.java, DATABASE_NAME)
                .addCallback(PulseSeedCallback)
                .addMigrations(*PulseMigrations.ALL)
                .build()
        }
    }
}

private object PulseSeedCallback : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        PulseSeedData.seed(db)
    }
}
