package com.example.dating.core.auth

import android.util.Base64
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object JwtUtils {

    fun getUserId(token: String): Int? {

        return try {

            val parts = token.split(".")

            if (parts.size < 2) {
                return null
            }

            val payload =
                String(
                    Base64.decode(
                        parts[1],
                        Base64.URL_SAFE or Base64.NO_WRAP
                    )
                )

            val jsonObject =
                Json.parseToJsonElement(payload)
                    .jsonObject

            jsonObject["id"]
                ?.jsonPrimitive
                ?.content
                ?.toInt()

        } catch (e: Exception) {

            null
        }
    }
}