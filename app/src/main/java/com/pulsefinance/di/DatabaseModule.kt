package com.pulsefinance.di

import android.content.Context
import com.pulsefinance.data.local.dao.BudgetDao
import com.pulsefinance.data.local.dao.CategoryDao
import com.pulsefinance.data.local.dao.CategoryKeywordDao
import com.pulsefinance.data.local.dao.ExpenseDao
import com.pulsefinance.data.local.dao.RecurringRuleDao
import com.pulsefinance.data.local.database.PulseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PulseDatabase =
        PulseDatabase.create(context)

    @Provides
    fun provideExpenseDao(database: PulseDatabase): ExpenseDao = database.expenseDao()

    @Provides
    fun provideCategoryDao(database: PulseDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideCategoryKeywordDao(database: PulseDatabase): CategoryKeywordDao = database.categoryKeywordDao()

    @Provides
    fun provideRecurringRuleDao(database: PulseDatabase): RecurringRuleDao = database.recurringRuleDao()

    @Provides
    fun provideBudgetDao(database: PulseDatabase): BudgetDao = database.budgetDao()
}
