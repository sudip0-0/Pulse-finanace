package com.pulsefinance.di

import com.pulsefinance.domain.repository.BudgetRepository
import com.pulsefinance.domain.repository.CategoryRepository
import com.pulsefinance.domain.repository.ExpenseRepository
import com.pulsefinance.domain.usecase.CalculateBudgetProgressUseCase
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
}
