package com.indie.stockapp.features.stocks.data.remote

import androidx.compose.ui.res.stringResource
import com.indie.stockapp.R
import com.indie.stockapp.core.resources.ResourceProvider
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockWebSocketClient @Inject constructor(
    private val client: OkHttpClient,
    private val resourceProvider: ResourceProvider
) {
    private var webSocket: WebSocket? = null

    fun connect(url: String): Flow<WebSocketEvent> = callbackFlow {
        val request = Request.Builder().url(url).build()
        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(WebSocketEvent.OnOpen)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySend(WebSocketEvent.OnMessage(text))
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                trySend(WebSocketEvent.OnClosed)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySend(WebSocketEvent.OnError(t))
            }
        }

        webSocket = client.newWebSocket(request, listener)

        awaitClose {
            webSocket?.close(1000, resourceProvider.getString(R.string.lbl_closed_by_user))
        }
    }

    fun sendMessage(text: String) {
        webSocket?.send(text)
    }

    fun close() {
        webSocket?.close(1000, resourceProvider.getString(R.string.lbl_closed_by_user))
        webSocket = null
    }
}

sealed class WebSocketEvent {
    object OnOpen : WebSocketEvent()
    data class OnMessage(val text: String) : WebSocketEvent()
    object OnClosed : WebSocketEvent()
    data class OnError(val throwable: Throwable) : WebSocketEvent()
}
