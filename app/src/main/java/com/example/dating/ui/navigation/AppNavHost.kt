package com.example.dating.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.dating.ui.chat.*
import com.example.dating.ui.profile.ProfileScreen
import com.example.dating.ui.traditional.TraditionalMatchingHomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBar =
        currentRoute?.startsWith("conversation") == true ||
                currentRoute?.startsWith("chat_options") == true

    Scaffold(
        bottomBar = {
            if (!hideBottomBar) {
                BottomBar(navController)
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Screen.Traditional.route,
            modifier = modifier.padding(paddingValues)
        ) {

            composable(Screen.Traditional.route) {
                TraditionalMatchingHomeScreen()
            }

            composable(Screen.Chat.route) {
                ChatScreen(
                    onConversationSelected = { conversation ->
                        navController.navigate(
                            Screen.Conversation.createRoute(conversation.matchId)
                        )
                    }
                )
            }

            composable(
                route = Screen.Conversation.route,
                arguments = listOf(
                    navArgument("matchId") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val matchId = backStackEntry.arguments?.getInt("matchId")

                val conversation = MockChatData.generateConversations()
                    .find { it.matchId == matchId }

                conversation?.let {
                    ConversationScreen(
                        conversation = it,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onAvatarClick = {
                            navController.navigate(
                                Screen.ChatOptions.createRoute(it.matchId)
                            )
                        }
                    )
                }
            }

            composable(
                route = Screen.ChatOptions.route,
                arguments = listOf(
                    navArgument("matchId") { type = NavType.IntType }
                )
            ) { backStackEntry ->

                val matchId = backStackEntry.arguments?.getInt("matchId")

                val conversation = MockChatData.generateConversations()
                    .find { it.matchId == matchId }

                conversation?.let {
                    ChatOptionsScreen(
                        user = it.user,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }
        }
    }
}