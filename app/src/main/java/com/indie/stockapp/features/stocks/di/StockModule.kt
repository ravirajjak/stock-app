package com.indie.stockapp.features.stocks.di

import com.indie.stockapp.features.stocks.data.repository.StockRepositoryImpl
import com.indie.stockapp.features.stocks.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StockModule {

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        stockRepositoryImpl: StockRepositoryImpl
    ): StockRepository
}
