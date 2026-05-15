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

package com.example.dating.data.remote

import com.example.dating.data.model.ApiResponse
import com.example.dating.data.model.LoginRequest
import com.example.dating.data.model.LoginResponseData
import com.example.dating.data.model.RegisterRequest
import com.example.dating.data.model.RegisterResponseData
import com.example.dating.data.model.RefreshTokenResponseData
import com.example.dating.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * A public interface that exposes authentication endpoints.
 * Base path: /api (configured in RetrofitClient)
 */
interface AuthApiService {
    /**
     * Register a new user account
     * POST /api/auth/register
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<RegisterResponseData>

    /**
     * Login with email and password
     * POST /api/auth/login
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponseData>

    /**
     * Fetch current authenticated user profile
     * GET /api/users/preferences
     * Requires: Authorization header with Bearer token
     */
    @GET("users/preferences")
    suspend fun getMe(
        @Header("Authorization") authHeader: String
    ): ApiResponse<User>

    /**
     * Refresh authentication token
     * POST /api/auth/refresh-token
     * Requires: Authorization header with Bearer token
     */
    @POST("auth/refresh-token")
    suspend fun refreshToken(
        @Header("Authorization") authHeader: String
    ): ApiResponse<RefreshTokenResponseData>
}
