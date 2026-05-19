package com.example.dating.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dating.core.socket.SocketManager
import com.example.dating.ui.auth.AuthViewModel
import com.example.dating.ui.navigation.AuthNavHost
import com.example.dating.ui.navigation.AppNavHost

@Composable
fun AppRoot(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
) {
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
    var userId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus { isAuth ->
//            isLoggedIn = isAuth
//            if (isAuth) {
//                userId = 1 // lấy từ token đã lưu
//            }
            isLoggedIn = true
            if (true) {
                userId = 1 // lấy từ token đã lưu
            }
        }
    }

    LaunchedEffect(isLoggedIn, userId) {

        if (isLoggedIn == true && userId != null) {

            SocketManager.connect(userId!!)
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
