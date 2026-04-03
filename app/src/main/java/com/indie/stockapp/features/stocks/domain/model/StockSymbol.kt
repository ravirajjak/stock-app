package com.indie.stockapp.features.stocks.domain.model

data class StockSymbol(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val description: String = ""
) {
    val isPriceIncreased: Boolean?
        get() = when {
            currentPrice > previousPrice -> true
            currentPrice < previousPrice -> false
            else -> null
        }
}
