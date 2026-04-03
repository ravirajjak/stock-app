package com.indie.stockapp.features.stocks.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class StockDto(
    val symbol: String,
    val price: Double
)
