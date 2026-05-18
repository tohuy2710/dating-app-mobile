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

package com.example.dating.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ============== LOGIN ==============

/**
 * Login request data class for sending credentials to the backend API.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * User data class from API response
 */
@Serializable
data class User(
    @SerialName("user_id")
    val userId: Int,
    val email: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("birth_date")
    val birthDate: String? = null,
    val gender: String? = null,
    val bio: String? = null,
    @SerialName("created_at")
    val createdAt: String
)

/**
 * Login response data class received from the backend API.
 * Matches the actual backend response structure.
 */
@Serializable
data class LoginResponseData(
    val user: User,
    val token: String
)

@Serializable
data class LoginResponse(
    val message: String,
    val token: String,
    @SerialName("token_type")
    val tokenType: String = "Bearer",
    @SerialName("expires_in")
    val expiresIn: Long = 604800, // 7 days in seconds
    @SerialName("expires_at")
    val expiresAt: String
)

// ============== REGISTER ==============

/**
 * Register request data class for sending user registration data to the backend API.
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("birth_date")
    val birthDate: String? = null,
    val gender: String? = null,
    val bio: String? = null
)

/**
 * Register response data class received from the backend API.
 */
@Serializable
data class RegisterResponseData(
    val user: User,
    val token: String
)

// ============== USER PREFERENCES ==============

/**
 * Request data class for updating user preferences.
 */
@Serializable
data class UserPreferencesRequest(
    @SerialName("target_gender")
    val targetGender: String?,
    @SerialName("min_age")
    val minAge: Int,
    @SerialName("max_age")
    val maxAge: Int,
    @SerialName("max_distance_km")
    val maxDistanceKm: Int,
    @SerialName("anonymous_interests")
    val anonymousInterests: List<String>
)

/**
 * Response data class for user preferences operations.
 */
@Serializable
data class UserPreferencesResponse(
    val message: String = ""
)

// ============== REFRESH TOKEN ==============

/**
 * Refresh token response data class
 */
@Serializable
data class RefreshTokenResponseData(
    val token: String
)

// ============== LOCAL STORAGE ==============

/**
 * Token data class for storing in SQLite.
 */
@Serializable
data class TokenEntity(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresAt: String,
    val createdAt: Long = System.currentTimeMillis()
)
