package com.example.dating.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dating.ui.auth.AuthViewModel
import com.example.dating.ui.navigation.AuthNavHost
import com.example.dating.ui.navigation.AppNavHost

@Composable
fun AppRoot(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus { isAuth ->
            isLoggedIn = isAuth
        }
    }

    val authNavController = rememberNavController()
    val mainNavController = rememberNavController()

    if (isLoggedIn == null) {
        return
    }

    if (isLoggedIn == false) {
        AuthNavHost(
            navController = authNavController,
            onAuthSuccess = {
                isLoggedIn = true
            }
        )
    } else {
        AppNavHost(
            navController = mainNavController
        )
    }
}
