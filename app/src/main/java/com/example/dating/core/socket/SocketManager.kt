package com.example.dating.core.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import com.example.dating.core.config.AppConfig

object SocketManager {

    private var socket: Socket? = null
    private var isConnected = false


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

                    Log.d("SOCKET", data.toString())
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