/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dating.core.network

import com.example.dating.core.config.AppConfig
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import com.example.dating.core.auth.TokenManager

/**
 * Singleton object for Retrofit client configuration.
 * Provides a single instance of Retrofit configured with OkHttp and kotlinx.serialization.
 */
object RetrofitClient {

    // Update this to your backend URL
    // For localhost development: http://10.0.2.2:3000 (for Android Emulator)
    // For physical device: http://your-machine-ip:3000
    private const val BASE_URL = "http://192.168.1.19:5000/"

//    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Authentication interceptor that adds JWT token to all requests.
     * Adds: Authorization: Bearer {token}
     */
    private val authInterceptor = Interceptor { chain ->

        val originalRequest = chain.request()

        val builder = originalRequest.newBuilder()

        val token = TokenManager.getToken()

        if (!token.isNullOrEmpty()) {
            builder.header(
                "Authorization",
                "Bearer $token"
            )
        }

        chain.proceed(builder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)  // Add auth interceptor first
        .addInterceptor(loggingInterceptor)  // Then logging
        .connectTimeout(AppConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(AppConfig.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(AppConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    val retrofitInstance: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
