package com.indie.stockapp.features.stocks.data.repository

import com.indie.stockapp.features.stocks.data.remote.StockDto
import com.indie.stockapp.features.stocks.data.remote.StockWebSocketClient
import com.indie.stockapp.features.stocks.data.remote.WebSocketEvent
import com.indie.stockapp.features.stocks.domain.model.StockSymbol
import com.indie.stockapp.features.stocks.domain.repository.StockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val webSocketClient: StockWebSocketClient
) : StockRepository {

    private val symbols = listOf(
        "AAPL",
        "GOOGL",
        "MSFT",
        "AMZN",
        "TSLA",
        "NVDA",
        "META",
        "BRK.B",
        "V",
        "JNJ",
        "WMT",
        "JPM",
        "PG",
        "MA",
        "UNH",
        "HD",
        "DIS",
        "CRM",
        "PYPL",
        "BAC",
        "INTC",
        "CMCSA",
        "XOM",
        "ADBE",
        "NFLX"
    )

    private val _stockPrices = MutableStateFlow<List<StockSymbol>>(
        symbols.map { StockSymbol(it, 150.0 + Random.nextDouble(-10.0, 10.0), 150.0) })
    private val _isConnected = MutableStateFlow(false)
    private val _isFeedActive = MutableStateFlow(false)

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var feedJob: Job? = null

    // Buffer to batch updates and reduce UI jank
    private val updateBuffer = mutableListOf<StockDto>()

    init {
        repositoryScope.launch {
            webSocketClient.connect("wss://ws.postman-echo.com/raw").collect { event ->
                when (event) {
                    is WebSocketEvent.OnOpen -> _isConnected.value = true
                    is WebSocketEvent.OnMessage -> {
                        synchronized(updateBuffer) {
                            try {
                                updateBuffer.add(Json.decodeFromString<StockDto>(event.text))
                            } catch (e: Exception) {
                            }
                        }
                    }

                    is WebSocketEvent.OnClosed -> _isConnected.value = false
                    is WebSocketEvent.OnError -> _isConnected.value = false
                }
            }
        }

        // Process buffer every 500ms
        repositoryScope.launch {
            while (true) {
                delay(500)
                val updates = synchronized(updateBuffer) {
                    val list = updateBuffer.toList()
                    updateBuffer.clear()
                    list
                }
                if (updates.isNotEmpty()) {
                    applyBatchUpdates(updates)
                }
            }
        }
    }

    private fun applyBatchUpdates(updates: List<StockDto>) {
        _stockPrices.update { currentList ->
            val newList = currentList.toMutableList()
            updates.forEach { update ->
                val index = newList.indexOfFirst { it.symbol == update.symbol }
                if (index != -1) {
                    val old = newList[index]
                    newList[index] =
                        old.copy(currentPrice = update.price, previousPrice = old.currentPrice)
                }
            }
            newList.sortedByDescending { it.currentPrice }
        }
    }

    override fun getStockPriceStream(): Flow<List<StockSymbol>> = _stockPrices.asStateFlow()
    override fun getConnectionStatus(): Flow<Boolean> = _isConnected.asStateFlow()
    override fun isFeedActive(): Flow<Boolean> = _isFeedActive.asStateFlow()

    override fun startFeed() {
        if (_isFeedActive.value) return
        _isFeedActive.value = true
        feedJob = repositoryScope.launch {
            while (_isFeedActive.value) {
                symbols.forEach { symbol ->
                    val current =
                        _stockPrices.value.find { it.symbol == symbol }?.currentPrice ?: 150.0
                    val nextPrice = current + Random.nextDouble(-5.0, 5.0)
                    webSocketClient.sendMessage(Json.encodeToString(StockDto(symbol, nextPrice)))
                }
                delay(1000)
            }
        }
    }

    override fun stopFeed() {
        _isFeedActive.value = false
        feedJob?.cancel()
    }
}
