package com.example.dating.core.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import com.example.dating.core.config.AppConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SocketManager {

    private var socket: Socket? = null
    private var isConnected = false

    private val _anonymousMatchEvent = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val anonymousMatchEvent = _anonymousMatchEvent.asSharedFlow()

    private val _newMessageEvent = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val newMessageEvent = _newMessageEvent.asSharedFlow()

    fun connect(
        userId: Int
    ) {
        if (isConnected) return

        try {

            socket = IO.socket(
                AppConfig.API_BASE_URL
            )

            socket?.connect()

            socket?.on(Socket.EVENT_CONNECT) {

                Log.d("SOCKET", "CONNECTED")

                socket?.emit(
                    "authenticate",
                    userId
                )
                isConnected = true
            }

            socket?.on("new_message") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject
                    Log.d("SOCKET", "New message received: $data")
                    // Bắn dữ liệu vào Flow
                    _newMessageEvent.tryEmit(data)
                }
            }

            socket?.on("anonymous_match") { args ->
                if (args.isNotEmpty()) {
                    val data = args[0] as JSONObject
                    Log.d("SOCKET", "Match Event Received: $data")
                    // Đẩy dữ liệu vào Flow để ViewModel nhận
                    _anonymousMatchEvent.tryEmit(data)
                }
            }

        } catch (e: Exception) {

            Log.e("SOCKET", "ERROR", e)
        }
    }

    fun disconnect() {
        socket?.disconnect()
        isConnected = false
    }

    fun sendMessage(
        matchId: Int,
        content: String
    ) {

        val json = JSONObject()

        json.put("match_id", matchId)
        json.put("content", content)

        socket?.emit("send_message", json)
    }
}