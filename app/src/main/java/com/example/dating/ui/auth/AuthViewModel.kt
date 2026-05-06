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

    var usernameInput: String by mutableStateOf("")
        private set

    var passwordInput: String by mutableStateOf("")
        private set

    fun updateUsername(newUsername: String) {
        usernameInput = newUsername
    }

    fun updatePassword(newPassword: String) {
        passwordInput = newPassword
    }

    /**
     * Performs login with the provided username and password.
     * Saves the token to local database upon successful login.
     */
    fun login() {
        if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
            loginUiState = LoginUiState.Error("Username and password are required")
            return
        }

        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            loginUiState = try {
                val result = authRepository.login(usernameInput, passwordInput)
                
                // Save token to local database
                authRepository.saveToken(result.token, result.tokenType, result.expiresAt)
                
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
            usernameInput = ""
            passwordInput = ""
            loginUiState = LoginUiState.Idle
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
