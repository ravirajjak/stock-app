package com.indie.stockapp.navigation

object Destinations {
    const val FEED = "feed"
    const val SYMBOL_ARG = "symbol"
    const val DETAILS_ROUTE = "details/{$SYMBOL_ARG}"

    fun detailsRoute(symbol: String): String = "details/$symbol"
}