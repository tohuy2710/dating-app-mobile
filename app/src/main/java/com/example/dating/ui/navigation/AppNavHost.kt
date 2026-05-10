package com.example.dating.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.dating.ui.chat.ChatScreen
import com.example.dating.ui.chat.ConversationScreen
import com.example.dating.ui.chat.ChatOptionsScreen
import com.example.dating.ui.chat.MockChatData
import com.example.dating.ui.profile.ProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Chat.route,
        modifier = modifier
    ) {

        composable(Screen.Chat.route) {

            ChatScreen(
                onConversationSelected = { conversation ->

                    navController.navigate(
                        Screen.Conversation.createRoute(
                            conversation.matchId
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.Conversation.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val matchId =
                backStackEntry.arguments?.getInt("matchId")

            val conversation =
                MockChatData.generateConversations()
                    .find { it.matchId == matchId }

            conversation?.let {

                ConversationScreen(
                    conversation = it,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onAvatarClick = {
                        navController.navigate(Screen.ChatOptions.createRoute(it.matchId))
                    }
                )
            }
        }

        composable(
            route = Screen.ChatOptions.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.IntType
                }
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

//        composable(Screen.Traditional.route) {
//            TraditionalMatchScreen()
//        }
//
//        composable(Screen.Anonymous.route) {
//            AnonymousMatchScreen()
//        }
    }
}