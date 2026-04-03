package com.indie.stockapp.features.stocks.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indie.stockapp.features.stocks.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StockFeedViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    val uiState: StateFlow<StockFeedUiState> = combine(
        repository.getStockPriceStream(),
        repository.getConnectionStatus(),
        repository.isFeedActive()
    ) { stocks, isConnected, isFeedActive ->
        StockFeedUiState(stocks, isConnected, isFeedActive)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StockFeedUiState()
    )

    fun toggleFeed() {
        if (uiState.value.isFeedActive) {
            repository.stopFeed()
        } else {
            repository.startFeed()
        }
    }
}