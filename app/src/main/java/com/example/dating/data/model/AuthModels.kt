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

/**
 * Login request data class for sending credentials to the backend API.
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Login response data class received from the backend API.
 * Matches the actual backend response structure.
 */
@Serializable
data class LoginResponse(
    val user: AuthUser,
    val token: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    @SerialName("full_name") val full_name: String,
    @SerialName("birth_date") val birth_date: String,
    val gender: String,
    val bio: String?,
    val image_url: String?,
    val anonymous_interests: List<String>? = null,
    val min_age: Int? = null,
    val max_age: Int? = null,
    val target_gender: String? = null,
    val max_distance_km: Int? = null,
)

@Serializable
data class AuthUser(
    @SerialName("user_id")
    val userId: Int,

    val email: String,

    @SerialName("full_name")
    val fullName: String,

    @SerialName("birth_date")
    val birthDate: String,

    val gender: String,

    val bio: String? = null,

    @SerialName("default_mode")
    val defaultMode: String? = null,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

)

/**
 * Token data class for storing in SQLite.
 */
@Serializable
data class TokenEntity(
    val token: String,
    val tokenType: String,
    val expiresAt: String,
    val createdAt: Long = System.currentTimeMillis()
)
