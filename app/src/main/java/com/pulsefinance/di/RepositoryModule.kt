package com.pulsefinance.di

import com.pulsefinance.data.local.dao.BudgetDao
import com.pulsefinance.data.local.dao.CategoryDao
import com.pulsefinance.data.local.dao.CategoryKeywordDao
import com.pulsefinance.data.local.dao.ExpenseDao
import com.pulsefinance.data.local.dao.RecurringRuleDao
import com.pulsefinance.data.repository.BudgetRepositoryImpl
import com.pulsefinance.data.repository.CategoryKeywordRepositoryImpl
import com.pulsefinance.data.repository.CategoryRepositoryImpl
import com.pulsefinance.data.repository.ExpenseRepositoryImpl
import com.pulsefinance.data.repository.RecurringRuleRepositoryImpl
import com.pulsefinance.domain.repository.BudgetRepository
import com.pulsefinance.domain.repository.CategoryKeywordRepository
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository
import com.pulsefinance.domain.repository.RecurringRuleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideExpenseRepository(dao: ExpenseDao): ExpenseRepository =
        ExpenseRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryRepository(dao: CategoryDao): CategoryRepository =
        CategoryRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideBudgetRepository(dao: BudgetDao): BudgetRepository =
        BudgetRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideRecurringRuleRepository(dao: RecurringRuleDao): RecurringRuleRepository =
        RecurringRuleRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideCategoryKeywordRepository(dao: CategoryKeywordDao): CategoryKeywordRepository =
        CategoryKeywordRepositoryImpl(dao)
}
