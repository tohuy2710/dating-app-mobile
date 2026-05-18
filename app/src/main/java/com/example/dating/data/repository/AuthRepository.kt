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
import com.example.dating.data.model.LoginResponseData
import com.example.dating.data.model.RegisterRequest
import com.example.dating.data.model.RegisterResponseData
import com.example.dating.data.model.User
import com.example.dating.data.model.UserPreferencesRequest
import com.example.dating.data.model.UserPreferencesResponse
import com.example.dating.data.remote.AuthApiService
import retrofit2.HttpException

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {
    /**
     * Performs registration with email, password, and user profile information.
     */
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        birthDate: String? = null,
        gender: String? = null,
        bio: String? = null
    ): RegisterResponseData

    /**
     * Performs login with email and password.
     */
    suspend fun login(email: String, password: String): LoginResponseData

    /**
     * Fetches the current authenticated user profile from /me.
     */
    suspend fun getMe(): User

    /**
     * Updates user preferences on the backend.
     */
    suspend fun saveUserPreferences(
        targetGender: String?,
        minAge: Int,
        maxAge: Int,
        maxDistanceKm: Int,
        anonymousInterests: List<String>
    ): UserPreferencesResponse

    /**
     * Refreshes the authentication token using the current token.
     */
    suspend fun refreshToken(token: String): String

    /**
     * Saves the login token to local database.
     */
    suspend fun saveToken(token: String, tokenType: String, expiresAt: String)

    /**
     * Retrieves the latest saved token from local database.
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

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        birthDate: String?,
        gender: String?,
        bio: String?
    ): RegisterResponseData {
        val request = RegisterRequest(
            email = email,
            password = password,
            fullName = fullName,
            birthDate = birthDate,
            gender = gender,
            bio = bio
        )
        val response = authApiService.register(request)
        if (!response.success) {
            throw Exception(response.message)
        }
        return response.data ?: throw Exception("No data in response")
    }

    override suspend fun login(email: String, password: String): LoginResponseData {
        val request = LoginRequest(email = email, password = password)
        val response = authApiService.login(request)
        if (!response.success) {
            throw Exception(response.message)
        }
        return response.data ?: throw Exception("No data in response")
    }

    override suspend fun getMe(): User {
        val tokenEntity = tokenDao.getLatestToken() ?: throw IllegalStateException("User is not authenticated")
        return try {
            val response = authApiService.getMe(buildAuthorizationHeader(tokenEntity))
            if (!response.success) {
                throw Exception(response.message)
            }
            response.data ?: throw Exception("No user data in response")
        } catch (e: HttpException) {
            if (e.code() == 404) {
                throw IllegalStateException("User profile not found. Please complete your profile setup.")
            }
            throw e
        }
    }

    override suspend fun saveUserPreferences(
        targetGender: String?,
        minAge: Int,
        maxAge: Int,
        maxDistanceKm: Int,
        anonymousInterests: List<String>
    ): UserPreferencesResponse {
        val tokenEntity = tokenDao.getLatestToken() ?: throw IllegalStateException("User is not authenticated")
        val request = UserPreferencesRequest(
            targetGender = targetGender,
            minAge = minAge,
            maxAge = maxAge,
            maxDistanceKm = maxDistanceKm,
            anonymousInterests = anonymousInterests
        )
        val response = authApiService.updatePreferences(buildAuthorizationHeader(tokenEntity), request)
        if (!response.success) {
            throw Exception(response.message)
        }
        return response.data ?: UserPreferencesResponse(message = response.message)
    }

    override suspend fun refreshToken(token: String): String {
        val response = authApiService.refreshToken("Bearer $token")
        if (!response.success) {
            throw Exception(response.message)
        }
        return response.data?.token ?: throw Exception("No token in response")
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

    private fun buildAuthorizationHeader(tokenEntity: TokenEntity): String {
        val tokenType = tokenEntity.tokenType.ifBlank { "Bearer" }
        return if (tokenEntity.token.startsWith("Bearer ", ignoreCase = true)) {
            tokenEntity.token
        } else {
            "$tokenType ${tokenEntity.token}"
        }
    }
}
