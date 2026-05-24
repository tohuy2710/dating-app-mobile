package com.example.dating.core.messaging

import android.content.Context
import android.util.Log
import com.example.dating.DatingApplication
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.remote.DeviceApiService
import com.example.dating.data.remote.RegisterDeviceRequest
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DeviceTokenManager {

    fun sendTokenToServer(
        context: Context,
        fcmToken: String
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                // Get the auth repository from the DI container
                val app = context.applicationContext as DatingApplication
                val authRepository = app.container.authRepository

                // Create DeviceApiService instance using Retrofit
                val deviceApiService = RetrofitClient.retrofitInstance.create(DeviceApiService::class.java)

                // The token is automatically added to the request headers by the auth interceptor
                val response = deviceApiService.registerDevice(
                    RegisterDeviceRequest(
                        deviceToken = fcmToken,
                        deviceType = "android"
                    )
                )

                Log.d("FCM", "REGISTER DEVICE SUCCESS")

            } catch (e: Exception) {

                Log.e("FCM", "REGISTER DEVICE ERROR", e)
            }
        }
    }

    suspend fun unregisterDevice(
        context: Context
    ) {

        FirebaseMessaging.getInstance()
            .token
            .addOnSuccessListener { token ->

                CoroutineScope(Dispatchers.IO).launch {

                    try {

                        val deviceApiService =
                            RetrofitClient.retrofitInstance.create(
                                DeviceApiService::class.java
                            )

                        deviceApiService.unregisterDevice(
                            RegisterDeviceRequest(
                                deviceToken = token,
                                deviceType = "android"
                            )
                        )

                        Log.d(
                            "FCM",
                            "UNREGISTER DEVICE SUCCESS"
                        )

                    } catch (e: Exception) {

                        Log.e(
                            "FCM",
                            "UNREGISTER DEVICE ERROR",
                            e
                        )
                    }
                }
            }
    }
}