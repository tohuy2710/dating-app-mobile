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
    // Trạng thái đăng nhập
    var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }

    // Kiểm tra token khi khởi chạy ứng dụng
    LaunchedEffect(Unit) {
        authViewModel.checkAuthStatus { isAuth ->
            isLoggedIn = isAuth
        }
    }

    val authNavController = rememberNavController()
    val mainNavController = rememberNavController()

    // Chờ kiểm tra trạng thái đăng nhập
    if (isLoggedIn == null) {
        // Có thể hiển thị màn hình Splash hoặc Loading ở đây
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
