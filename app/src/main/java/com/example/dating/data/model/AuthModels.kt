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
    val username: String,
    val password: String
)

/**
 * Login response data class received from the backend API.
 * Matches the actual backend response structure.
 */
@Serializable
data class LoginResponse(
    val message: String,
    val token: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("expires_at")
    val expiresAt: String
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
