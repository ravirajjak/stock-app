package com.indie.stockapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
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
            StockFeedScreen(
                onNavigateToDetails = { symbol ->
                    navController.navigate(Destinations.detailsRoute(symbol))
                }
            )
        }

        composable(
            route = Destinations.DETAILS_ROUTE,
            arguments = listOf(
                navArgument(Destinations.SYMBOL_ARG) {
                    type = NavType.StringType
                }
            ),
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "stocks://symbol/{${Destinations.SYMBOL_ARG}}"
                }
            )
        ) {
            StockDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}