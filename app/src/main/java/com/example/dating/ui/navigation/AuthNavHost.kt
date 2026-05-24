package com.example.dating.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.dating.ui.auth.AuthViewModel
import com.example.dating.ui.auth.LoginScreen
import com.example.dating.ui.auth.RegisterScreen
import com.example.dating.ui.auth.RegisterSharedViewModel
import com.example.dating.ui.preferences.PreferencesScreen

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
                    navController.navigate("register_graph")
                }
            )
        }

        navigation(
            startDestination = Screen.Register.route,
            route = "register_graph"
        ) {

            composable(Screen.Register.route) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("register_graph")
                }

                val sharedViewModel: RegisterSharedViewModel =
                    viewModel(parentEntry)

                RegisterScreen(
                    sharedViewModel = sharedViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNextPage = {
                        navController.navigate(Screen.Preferences.route)
                    }
                )
            }

            composable(Screen.Preferences.route) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("register_graph")
                }

                val sharedViewModel: RegisterSharedViewModel =
                    viewModel(parentEntry)

                PreferencesScreen(
                    sharedViewModel = sharedViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onComplete = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo("register_graph") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}