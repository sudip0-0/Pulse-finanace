package com.pulsefinance.di

import com.pulsefinance.data.receipt.MlKitReceiptTextRecognizer
import com.pulsefinance.domain.repository.ReceiptTextRecognizer
import com.pulsefinance.domain.usecase.ParseReceiptFromTextUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReceiptModule {

    @Provides
    @Singleton
    fun provideReceiptTextRecognizer(): ReceiptTextRecognizer = MlKitReceiptTextRecognizer()

    @Provides
    fun provideParseReceiptFromText(): ParseReceiptFromTextUseCase = ParseReceiptFromTextUseCase()
}
