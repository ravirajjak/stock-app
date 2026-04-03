package com.indie.stockapp.features.stocks.domain.repository

import com.indie.stockapp.features.stocks.domain.model.StockSymbol
import kotlinx.coroutines.flow.Flow

interface StockRepository {
    fun getStockPriceStream(): Flow<List<StockSymbol>>
    fun getConnectionStatus(): Flow<Boolean>
    fun startFeed()
    fun stopFeed()
    fun isFeedActive(): Flow<Boolean>
}
