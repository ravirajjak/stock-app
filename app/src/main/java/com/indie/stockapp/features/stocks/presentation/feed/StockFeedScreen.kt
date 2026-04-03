package com.indie.stockapp.features.stocks.presentation.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.indie.stockapp.R
import com.indie.stockapp.features.stocks.domain.model.StockSymbol
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockFeedScreen(
    onNavigateToDetails: (String) -> Unit,
    viewModel: StockFeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lbl_stock_feed)) },
                navigationIcon = {
                    val statusColor = if (uiState.isConnected) Color.Green else Color.Red
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = stringResource(R.string.lbl_status),
                        tint = statusColor,
                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                    )
                },
                actions = {
                    Button(
                        onClick = { viewModel.toggleFeed() },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text(if (uiState.isFeedActive) stringResource(R.string.lbl_stop) else stringResource(R.string.lbl_start))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(uiState.stocks, key = { it.symbol }) { stock ->
                StockItem(stock = stock, onClick = { onNavigateToDetails(stock.symbol) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun StockItem(stock: StockSymbol, onClick: () -> Unit) {
    var flashColor by remember { mutableStateOf(Color.Transparent) }

    LaunchedEffect(stock.currentPrice) {
        if (stock.isPriceIncreased != null) {
            flashColor =
                if (stock.isPriceIncreased == true) Color.Green.copy(alpha = 0.2f) else Color.Red.copy(
                    alpha = 0.2f
                )
            delay(1000)
            flashColor = Color.Transparent
        }
    }

    val animatedColor by animateColorAsState(
        targetValue = flashColor,
        animationSpec = tween(durationMillis = 500)
    )

    Surface(
        color = animatedColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = RowPlacement.SpaceBetween
        ) {
            Column {
                Text(text = stock.symbol, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = stringResource(R.string.lbl_real_time_update), fontSize = 12.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = String.format("%.2f", stock.currentPrice),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (stock.isPriceIncreased != null) {
                    Icon(
                        imageVector = if (stock.isPriceIncreased == true) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (stock.isPriceIncreased == true) Color.Green else Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private object RowPlacement {
    val SpaceBetween = Arrangement.SpaceBetween
}
