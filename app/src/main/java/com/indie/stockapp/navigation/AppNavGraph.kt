package com.indie.stockapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.indie.stockapp.features.stocks.presentation.detail.StockDetailsScreen
import com.indie.stockapp.features.stocks.presentation.feed.StockFeedScreen


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.FEED
    ) {
        composable(Destinations.FEED) {
            StockFeedScreen()
        }

        composable(
            route = Destinations.DETAILS_ROUTE,
        ) {
            StockDetailsScreen()
        }
    }
}