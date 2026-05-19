package com.example.dating.core.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DatingFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCM", "NEW TOKEN: $token")

        DeviceTokenManager.sendTokenToServer(
            applicationContext,
            token
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title =
            message.notification?.title
                ?: message.data["title"]
                ?: "New Message"

        val body =
            message.notification?.body
                ?: message.data["body"]
                ?: ""

        NotificationHelper.showNotification(
            applicationContext,
            title,
            body
        )
    }
}