package com.indie.stockapp.features.stocks.presentation.feed

import com.indie.stockapp.features.stocks.domain.model.StockSymbol

data class StockFeedUiState(
    val stocks: List<StockSymbol> = emptyList(),
    val isConnected: Boolean = false,
    val isFeedActive: Boolean = false
)