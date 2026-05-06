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
import com.example.dating.data.remote.AuthApiService

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    /**
     * Performs login with username and password.
     * @param username User username
     * @param password User password
     * @return LoginResponse with token and metadata
     */
    suspend fun login(username: String, password: String): LoginResponse

    /**
     * Saves the login token to local database.
     * @param token The token to save
     * @param tokenType The type of token (e.g., "Bearer")
     * @param expiresAt When the token expires
     */
    suspend fun saveToken(token: String, tokenType: String, expiresAt: String)

    /**
     * Retrieves the latest saved token from local database.
     * @return TokenEntity or null if no token exists
     */
    suspend fun getLatestToken(): TokenEntity?

    /**
     * Clears all tokens from local database (logout).
     */
    suspend fun clearTokens()
}

/**
 * Network Implementation of Repository for authentication operations.
 */
class NetworkAuthRepository(
    private val authApiService: AuthApiService,
    private val tokenDao: TokenDao
) : AuthRepository {

    override suspend fun login(username: String, password: String): LoginResponse {
        return authApiService.login(LoginRequest(username, password))
    }

    override suspend fun saveToken(token: String, tokenType: String, expiresAt: String) {
        val tokenEntity = TokenEntity(
            token = token,
            tokenType = tokenType,
            expiresAt = expiresAt,
            createdAt = System.currentTimeMillis()
        )
        tokenDao.insertToken(tokenEntity)
    }

    override suspend fun getLatestToken(): TokenEntity? {
        return tokenDao.getLatestToken()
    }

    override suspend fun clearTokens() {
        tokenDao.deleteAllTokens()
    }
}
