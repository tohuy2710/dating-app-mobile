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
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.local.DatingDatabase
import com.example.dating.data.remote.AuthApiService
import com.example.dating.data.repository.AuthRepository
import com.example.dating.data.repository.NetworkAuthRepository

/**
 * Dependency Injection container at the application level.
 * Manages the creation and lifecycle of all repository instances.
 */
interface AppContainer {
    val authRepository: AuthRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    /**
     * Database instance for local data storage
     */
    private val database: DatingDatabase by lazy {
        DatingDatabase.getDatabase(context)
    }

    /**
     * Retrofit service object for authentication endpoints
     */
    private val authApiService: AuthApiService by lazy {
        RetrofitClient.retrofitInstance.create(AuthApiService::class.java)
    }

    /**
     * DI implementation for authentication repository with database access
     */
    override val authRepository: AuthRepository by lazy {
        NetworkAuthRepository(authApiService, database.tokenDao())
    }
}
