package com.example.dating.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dating.ui.auth.LoginScreen
import com.example.dating.ui.auth.RegisterScreen
import com.example.dating.ui.auth.AuthViewModel

@Composable
fun AuthNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onAuthSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {

        composable(Screen.Login.route) {
            val authViewModel: AuthViewModel =
                viewModel(factory = AuthViewModel.Factory)

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
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
    }
}