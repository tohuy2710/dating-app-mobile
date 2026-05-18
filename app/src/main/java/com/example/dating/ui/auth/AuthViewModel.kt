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

package com.example.dating.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dating.DatingApplication
import com.example.dating.data.model.LoginResponseData
import com.example.dating.data.model.RegisterResponseData
import com.example.dating.data.model.UserPreferencesResponse
import com.example.dating.data.repository.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import retrofit2.HttpException
import java.io.IOException
import java.util.Base64

/**
 * UI state for the Login screen
 */
sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val response: LoginResponseData) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

/**
 * UI state for the Register screen
 */
sealed interface RegisterUiState {
    object Idle : RegisterUiState
    object Loading : RegisterUiState
    data class Success(val response: RegisterResponseData) : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

/**
 * UI state for the Preferences screen
 */
sealed interface PreferencesUiState {
    object Idle : PreferencesUiState
    object Loading : PreferencesUiState
    data class Success(val response: UserPreferencesResponse) : PreferencesUiState
    data class Error(val message: String) : PreferencesUiState
}

/**
 * ViewModel for handling authentication logic and login/register operations.
 */
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // ============== LOGIN STATE ==============
    
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    var loginEmail: String by mutableStateOf("")
        private set

    var loginPassword: String by mutableStateOf("")
        private set

    fun updateLoginEmail(newEmail: String) {
        loginEmail = newEmail
    }

    fun updateLoginPassword(newPassword: String) {
        loginPassword = newPassword
    }

    /**
     * Pre-fills login fields with provided credentials (used after successful registration)
     */
    fun prefillLoginCredentials(email: String, password: String) {
        loginEmail = email
        loginPassword = password
    }

    // ============== REGISTER STATE ==============

    var registerUiState: RegisterUiState by mutableStateOf(RegisterUiState.Idle)
        private set

    var registerEmail: String by mutableStateOf("")
        private set

    var registerPassword: String by mutableStateOf("")
        private set

    var registerFullName: String by mutableStateOf("")
        private set

    var registerBirthDate: String by mutableStateOf("")
        private set

    var registerGender: String by mutableStateOf("")
        private set

    var registerBio: String by mutableStateOf("")
        private set

    // ============== PREFERENCES STATE ==============

    var preferencesUiState: PreferencesUiState by mutableStateOf(PreferencesUiState.Idle)
        private set

    var selectedInterests: List<String> by mutableStateOf(emptyList())
        private set

    var targetGenderPreference: String? by mutableStateOf(null)
        private set

    var minAgePreference: String by mutableStateOf("")
        private set

    var maxAgePreference: String by mutableStateOf("")
        private set

    var maxDistancePreference: String by mutableStateOf("")
        private set

    fun updateRegisterEmail(newEmail: String) {
        registerEmail = newEmail
    }

    fun updateRegisterPassword(newPassword: String) {
        registerPassword = newPassword
    }

    fun updateRegisterFullName(newName: String) {
        registerFullName = newName
    }

    fun updateRegisterBirthDate(newDate: String) {
        registerBirthDate = newDate
    }

    fun updateRegisterGender(newGender: String) {
        registerGender = newGender
    }

    fun updateRegisterBio(newBio: String) {
        // Bio can be null or empty
        registerBio = newBio
    }

    fun savePreferences(
        interests: List<String>,
        targetGender: String?,
        minAge: String,
        maxAge: String,
        maxDistance: String
    ) {
        selectedInterests = interests
        targetGenderPreference = targetGender
        minAgePreference = minAge
        maxAgePreference = maxAge
        maxDistancePreference = maxDistance
    }

    // ============== AUTH OPERATIONS ==============

    /**
     * Checks if there's a valid token stored locally.
     * If found, sets the state to success to bypass login screen.
     */
    fun checkAuthStatus(onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Reuse a saved token when it is still valid.
            val tokenEntity = authRepository.getLatestToken()
            if (tokenEntity == null) {
                onFinished(false)
                return@launch
            }

            if (isTokenExpired(tokenEntity.token)) {
                authRepository.clearTokens()
                onFinished(false)
            } else {
                onFinished(true)
            }
        }
    }

    /**
     * Performs login with the provided email and password.
     * Saves the token to local database upon successful login.
     */
    fun login() {
        if (loginEmail.isEmpty() || loginPassword.isEmpty()) {
            loginUiState = LoginUiState.Error("Email and password are required")
            return
        }

        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            loginUiState = try {
                val result = authRepository.login(loginEmail, loginPassword)
                
                // Save token to local database
                authRepository.saveToken(result.token, "Bearer", result.token)
                
                // Clear password for security
                loginPassword = ""
                LoginUiState.Success(result)
            } catch (e: IOException) {
                LoginUiState.Error("Network error. Please check your connection.")
            } catch (e: HttpException) {
                LoginUiState.Error("Login failed. Please check your credentials.")
            } catch (e: Exception) {
                LoginUiState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    /**
     * Performs user registration with provided information.
     * Saves the token to local database upon successful registration.
     */
    fun register() {
        // Validation
        if (registerEmail.isEmpty() || registerPassword.isEmpty() || registerFullName.isEmpty()) {
            registerUiState = RegisterUiState.Error("Email, password, and full name are required")
            return
        }

        if (registerPassword.length < 6) {
            registerUiState = RegisterUiState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            registerUiState = RegisterUiState.Loading
            registerUiState = try {
                val result = authRepository.register(
                    email = registerEmail,
                    password = registerPassword,
                    fullName = registerFullName,
                    birthDate = registerBirthDate.ifEmpty { null },
                    gender = registerGender.ifEmpty { null },
                    bio = registerBio.ifEmpty { null }
                )
                
                // Save token to local database
                authRepository.saveToken(result.token, "Bearer", result.token)
                
                registerPassword = ""
                RegisterUiState.Success(result)
            } catch (e: IOException) {
                RegisterUiState.Error("Network error. Please check your connection.")
            } catch (e: HttpException) {
                RegisterUiState.Error("Registration failed. Please check your information.")
            } catch (e: Exception) {
                RegisterUiState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    /**
     * Submits the user preferences to the backend.
     */
    fun submitPreferences() {
        viewModelScope.launch {
            preferencesUiState = PreferencesUiState.Loading
            preferencesUiState = try {
                val response = authRepository.saveUserPreferences(
                    targetGender = when (targetGenderPreference) {
                        "Nam" -> "male"
                        "Nữ" -> "female"
                        else -> "both"
                    },
                    minAge = minAgePreference.toIntOrNull() ?: 18,
                    maxAge = maxAgePreference.toIntOrNull() ?: 99,
                    maxDistanceKm = maxDistancePreference.toIntOrNull() ?: 50,
                    anonymousInterests = selectedInterests
                )
                PreferencesUiState.Success(response)
            } catch (e: IOException) {
                PreferencesUiState.Error("Network error. Please check your connection.")
            } catch (e: HttpException) {
                PreferencesUiState.Error("Failed to save preferences. Please try again.")
            } catch (e: Exception) {
                PreferencesUiState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    /**
     * Refreshes the authentication token
     */
    fun refreshToken() {
        viewModelScope.launch {
            try {
                val currentToken = authRepository.getLatestToken()
                if (currentToken != null) {
                    val newToken = authRepository.refreshToken(currentToken.token)
                    authRepository.saveToken(newToken, "Bearer", newToken)
                }
            } catch (e: Exception) {
                // Token refresh failed, user may need to log in again
                logout()
            }
        }
    }

    /**
     * Resets the login state to Idle
     */
    fun resetLoginState() {
        loginUiState = LoginUiState.Idle
    }

    /**
     * Resets the register state to Idle
     */
    fun resetRegisterState() {
        registerUiState = RegisterUiState.Idle
    }

    /**
     * Resets the preferences state to Idle
     */
    fun resetPreferencesState() {
        preferencesUiState = PreferencesUiState.Idle
    }

    /**
     * Logs out the user by clearing tokens and resetting state
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.clearTokens()
            loginEmail = ""
            loginPassword = ""
            registerEmail = ""
            registerPassword = ""
            registerFullName = ""
            registerBirthDate = ""
            registerGender = ""
            registerBio = ""
            selectedInterests = emptyList()
            targetGenderPreference = null
            minAgePreference = ""
            maxAgePreference = ""
            maxDistancePreference = ""
            loginUiState = LoginUiState.Idle
            registerUiState = RegisterUiState.Idle
            preferencesUiState = PreferencesUiState.Idle
        }
    }

    private fun isTokenExpired(token: String): Boolean {
        val rawToken = token.removePrefix("Bearer ").trim()
        val payload = rawToken.split(".").getOrNull(1) ?: return false
        return try {
            val decodedPayload = String(Base64.getUrlDecoder().decode(payload), Charsets.UTF_8)
            val expiresAtSeconds = Json.parseToJsonElement(decodedPayload)
                .jsonObject["exp"]
                ?.jsonPrimitive
                ?.longOrNull
                ?: return false

            val nowSeconds = System.currentTimeMillis() / 1000
            expiresAtSeconds <= nowSeconds
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Factory for [AuthViewModel] that takes [AuthRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DatingApplication)
                val authRepository = application.container.authRepository
                AuthViewModel(authRepository = authRepository)
            }
        }
    }
}
