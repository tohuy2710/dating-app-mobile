package com.example.dating.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.dating.ui.navigation.AuthNavHost
import com.example.dating.ui.navigation.AppNavHost

@Composable
fun AppRoot() {

    // giả lập login state (sau này thay bằng token)
    var isLoggedIn by remember { mutableStateOf(false) }

    val authNavController = rememberNavController()
    val mainNavController = rememberNavController()

    if (!isLoggedIn) {
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
