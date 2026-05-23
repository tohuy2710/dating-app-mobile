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
import com.example.dating.core.auth.JwtUtils
import com.example.dating.core.auth.TokenManager
import com.example.dating.data.model.LoginResponse
import com.example.dating.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Login screen
 */
sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val response: LoginResponse) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

/**
 * ViewModel for handling authentication logic and login operations.
 */
class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    /** The mutable State that stores the status of the login request */
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Idle)
        private set

    var emailInput: String by mutableStateOf("")
        private set

    var passwordInput: String by mutableStateOf("")
        private set

    fun updateEmail(newEmail: String) {
        emailInput = newEmail
    }

    fun updatePassword(newPassword: String) {
        passwordInput = newPassword
    }

    /**
     * Checks if there's a valid token stored locally.
     * If found, sets the state to success to bypass login screen.
     */
    fun checkAuthStatus(onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            val token = authRepository.getLatestToken()
            if (token != null) {
                // Here you might want to verify the token with the server
                // For now, we assume if it exists, it's valid or we'll handle 401 later
                onFinished(true)
            } else {
                onFinished(false)
            }
        }
    }

    /**
     * Performs login with the provided username and password.
     * Saves the token to local database upon successful login.
     */
    fun login() {
        if (emailInput.isEmpty() || passwordInput.isEmpty()) {
            loginUiState = LoginUiState.Error("Username and password are required")
            return
        }

        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            loginUiState = try {
                val result = authRepository.login(emailInput, passwordInput)
                
                // Save token to local database
                authRepository.saveToken(result.token)
                TokenManager.setToken(result.token)
                
                // Clear password for security
                passwordInput = ""
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
     * Resets the login state to Idle
     */
    fun resetState() {
        loginUiState = LoginUiState.Idle
    }

    /**
     * Logs out the user by clearing tokens and resetting state
     */
    fun logout() {
        viewModelScope.launch {
            authRepository.clearTokens()
            emailInput = ""
            passwordInput = ""
            loginUiState = LoginUiState.Idle
        }
    }

    fun checkAuthStatus(
        onFinished: (Boolean, Int?) -> Unit
    ) {

        viewModelScope.launch {

            try {

                val tokenEntity =
                    authRepository.getLatestToken()

                if (tokenEntity == null) {

                    onFinished(false, null)

                    return@launch
                }

                // set token vào memory
                TokenManager.setToken(
                    tokenEntity.token
                )

                // interceptor sẽ tự attach token
                val isValid =
                    authRepository.checkToken()

                if (!isValid) {

                    authRepository.clearTokens()

                    TokenManager.clearToken()

                    onFinished(false, null)

                    return@launch
                }

                val userId =
                    JwtUtils.getUserId(
                        tokenEntity.token
                    )

                onFinished(
                    true,
                    userId
                )


            } catch (e: Exception) {

                authRepository.clearTokens()

                TokenManager.clearToken()

                onFinished(false, null)
            }
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
