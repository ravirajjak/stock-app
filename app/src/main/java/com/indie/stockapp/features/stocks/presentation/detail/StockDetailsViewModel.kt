package com.indie.stockapp.features.stocks.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.indie.stockapp.features.stocks.domain.model.StockSymbol
import com.indie.stockapp.features.stocks.domain.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StockDetailsUiState(
    val stock: StockSymbol? = null,
    val description: String = ""
)

@HiltViewModel
class StockDetailsViewModel @Inject constructor(
    private val repository: StockRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val symbol: String = checkNotNull(savedStateHandle["symbol"])

    val uiState: StateFlow<StockDetailsUiState> = repository.getStockPriceStream()
        .map { stocks ->
            val stock = stocks.find { it.symbol == symbol }
            StockDetailsUiState(
                stock = stock,
                description = getMockDescription(symbol)
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StockDetailsUiState()
        )

    private fun getMockDescription(symbol: String): String {
        return "Detailed information about $symbol. This is a real-time tracking of $symbol stock price echoed through Postman WebSocket."
    }
}
