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

package com.example.dating.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dating.ui.auth.AuthViewModel
import com.example.dating.ui.auth.LoginScreen

/**
 * Root composable that handles the app entry point.
 * Displays the login screen for authentication.
 */
@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    // Get auth view model
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)

    // Show login screen
    LoginScreen(
        authViewModel = authViewModel,
        onLoginSuccess = {
            // Handle successful login - e.g., navigate to main app content
            // TODO: Add navigation to main app screen after successful login
        },
        modifier = modifier
    )
}
