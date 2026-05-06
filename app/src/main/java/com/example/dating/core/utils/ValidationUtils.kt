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

package com.example.dating.core.utils

/**
 * Utility functions and extensions for common operations.
 * Add helper functions for:
 * - Email validation
 * - Password validation
 * - String formatting
 * - Date formatting
 * - Error message handling
 * - Network utilities
 */

/**
 * Validates if the given string is a valid email format.
 */
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
    return this.matches(emailRegex)
}

/**
 * Validates if the password meets minimum requirements.
 */
fun String.isValidPassword(): Boolean {
    // Minimum 6 characters
    return this.length >= 6
}
