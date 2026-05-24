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

package com.example.dating.data.repository

import com.example.dating.data.local.TokenDao
import com.example.dating.data.local.TokenEntity
import com.example.dating.data.model.LoginRequest
import com.example.dating.data.model.LoginResponse
import com.example.dating.data.model.RegisterRequest
import com.example.dating.data.remote.AuthApiService

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): LoginResponse

    suspend fun register(request: RegisterRequest): LoginResponse

    suspend fun checkToken(): Boolean

    suspend fun saveToken(token: String)

    suspend fun getLatestToken(): TokenEntity?

    suspend fun clearTokens()
}

/**
 * Network Implementation of Repository for authentication operations.
 */
class NetworkAuthRepository(
    private val authApiService: AuthApiService,
    private val tokenDao: TokenDao
) : AuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): LoginResponse {

        return authApiService
            .login(LoginRequest(email, password))
            .data
    }

    override suspend fun register(request: RegisterRequest): LoginResponse {
        return authApiService
            .register(request)
            .data
    }

    override suspend fun saveToken(token: String) {

        val tokenEntity =
            TokenEntity(
                token = token
            )

        tokenDao.insertToken(tokenEntity)
    }

    override suspend fun checkToken(): Boolean {

        return try {

            authApiService.checkToken().data

        } catch (e: Exception) {

            false
        }
    }

    override suspend fun getLatestToken(): TokenEntity? {
        return tokenDao.getLatestToken()
    }

    override suspend fun clearTokens() {
        tokenDao.deleteAllTokens()
    }
}
