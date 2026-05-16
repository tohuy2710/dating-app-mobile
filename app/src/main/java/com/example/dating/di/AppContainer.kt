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

package com.example.dating.di

import android.content.Context
import com.example.dating.core.network.AuthInterceptor
import com.example.dating.data.local.DatingDatabase
import com.example.dating.data.remote.AuthApiService
import com.example.dating.data.remote.MatchingApiService
import com.example.dating.data.repository.AuthRepository
import com.example.dating.data.repository.MatchingRepository
import com.example.dating.data.repository.NetworkAuthRepository
import com.example.dating.data.repository.NetworkMatchingRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

/**
 * Dependency Injection container at the application level.
 * Manages the creation and lifecycle of all repository instances.
 */
interface AppContainer {
    val authRepository: AuthRepository
    val matchingRepository: MatchingRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    private val BASE_URL = "http://192.168.0.110:8080/api/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Database instance for local data storage
     */
    private val database: DatingDatabase by lazy {
        DatingDatabase.getDatabase(context)
    }

    /**
     * Logging interceptor for debugging network requests
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Auth interceptor to add tokens to requests
     */
    private val authInterceptor by lazy {
        AuthInterceptor(database.tokenDao())
    }

    /**
     * OkHttpClient with auth and logging interceptors
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Retrofit instance
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    /**
     * Retrofit service object for authentication endpoints
     */
    private val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    /**
     * Retrofit service object for matching endpoints
     */
    private val matchingApiService: MatchingApiService by lazy {
        retrofit.create(MatchingApiService::class.java)
    }

    /**
     * DI implementation for authentication repository with database access
     */
    override val authRepository: AuthRepository by lazy {
        NetworkAuthRepository(authApiService, database.tokenDao())
    }

    /**
     * DI implementation for matching repository
     */
    override val matchingRepository: MatchingRepository by lazy {
        NetworkMatchingRepository(matchingApiService)
    }
}
