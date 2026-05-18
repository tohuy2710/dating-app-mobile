package com.example.dating.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dating.ui.auth.LoginScreen
import com.example.dating.ui.auth.RegisterScreen
import com.example.dating.ui.preferences.PreferencesScreen
import com.example.dating.ui.auth.AuthViewModel
import com.example.dating.ui.auth.PreferencesUiState

@Composable
fun AuthNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onAuthSuccess: () -> Unit
) {
    // Create a single AuthViewModel instance for both Login and Register screens
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = onAuthSuccess,
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = { email, password ->
                    navController.navigate(Screen.Preferences.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Preferences.route) {
            LaunchedEffect(authViewModel.preferencesUiState) {
                if (authViewModel.preferencesUiState is PreferencesUiState.Success) {
                    // Pre-fill login credentials with registered account
                    authViewModel.prefillLoginCredentials(
                        authViewModel.registerEmail,
                        authViewModel.registerPassword
                    )
                    authViewModel.resetPreferencesState()
                    // Navigate back to login screen after preferences are completed
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            }

            PreferencesScreen(
                preferencesUiState = authViewModel.preferencesUiState,
                onComplete = { selectedInterests, targetGender, minAge, maxAge, maxDistance ->
                    authViewModel.savePreferences(
                        interests = selectedInterests,
                        targetGender = targetGender,
                        minAge = minAge,
                        maxAge = maxAge,
                        maxDistance = maxDistance
                    )
                    authViewModel.submitPreferences()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
