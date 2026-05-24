package com.example.dating.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.dating.core.messaging.DeviceTokenManager
import com.example.dating.core.socket.SocketManager
import com.example.dating.ui.auth.AuthViewModel
import com.example.dating.ui.navigation.AuthNavHost
import com.example.dating.ui.navigation.AppNavHost
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppRoot(
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory),
    initialMatchId: Int? = null
) {

    val context = LocalContext.current

    var isLoggedIn by remember {
        mutableStateOf<Boolean?>(null)
    }

    var userId by remember {
        mutableStateOf<Int?>(null)
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        authViewModel.checkAuthStatus { isAuth, id ->

            isLoggedIn = isAuth

            if (isAuth) {
                userId = id
            }
        }
    }

    LaunchedEffect(isLoggedIn, userId) {

        if (isLoggedIn == true && userId != null) {

            SocketManager.connect(userId!!)

            FirebaseMessaging.getInstance()
                .token
                .addOnSuccessListener { token ->

                    DeviceTokenManager.sendTokenToServer(
                        context = context,
                        fcmToken = token
                    )
                }
        }
    }

    key(isLoggedIn) {

        val authNavController = rememberNavController()

        val mainNavController = rememberNavController()

        when (isLoggedIn) {

            null -> {
                // loading state
            }

            false -> {

                AuthNavHost(
                    navController = authNavController,
                    onAuthSuccess = {

                        authViewModel.checkAuthStatus { _, id ->

                            userId = id
                            isLoggedIn = true
                        }
                    }
                )
            }

            true -> {

                AppNavHost(
                    navController = mainNavController,
                    initialMatchId = initialMatchId,
                    currentUserId = userId,
                    onLogout = {
                        scope.launch {

                            DeviceTokenManager.unregisterDevice(context)

                            authViewModel.logout()

                            SocketManager.disconnect()

                            userId = null
                            isLoggedIn = false
                        }
                    }
                )
            }
        }
    }
}
