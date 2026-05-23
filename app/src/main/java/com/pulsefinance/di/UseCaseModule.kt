package com.pulsefinance.di

import com.pulsefinance.domain.repository.BudgetRepository
import com.pulsefinance.domain.repository.CategoryKeywordRepository
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository
import com.pulsefinance.domain.usecase.AddExpenseUseCase
import com.pulsefinance.domain.usecase.CalculateBudgetProgressUseCase
import com.pulsefinance.domain.usecase.CategorizeExpenseUseCase
import com.pulsefinance.domain.usecase.ObserveDashboardUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideCalculateBudgetProgress(): CalculateBudgetProgressUseCase =
        CalculateBudgetProgressUseCase()

    @Provides
    fun provideObserveDashboard(
        expenseRepository: ExpenseRepository,
        categoryRepository: CategoryRepository,
        budgetRepository: BudgetRepository,
        calculateBudgetProgress: CalculateBudgetProgressUseCase,
    ): ObserveDashboardUseCase = ObserveDashboardUseCase(
        expenseRepository = expenseRepository,
        categoryRepository = categoryRepository,
        budgetRepository = budgetRepository,
        calculateBudgetProgress = calculateBudgetProgress,
    )

    @Provides
    fun provideAddExpense(
        expenseRepository: ExpenseRepository,
        categoryRepository: CategoryRepository,
    ): AddExpenseUseCase = AddExpenseUseCase(
        expenseRepository = expenseRepository,
        categoryRepository = categoryRepository,
    )

    @Provides
    fun provideCategorizeExpense(
        categoryRepository: CategoryRepository,
        keywordRepository: CategoryKeywordRepository,
        expenseRepository: ExpenseRepository,
    ): CategorizeExpenseUseCase = CategorizeExpenseUseCase(
        categoryRepository = categoryRepository,
        keywordRepository = keywordRepository,
        expenseRepository = expenseRepository,
    )
}
