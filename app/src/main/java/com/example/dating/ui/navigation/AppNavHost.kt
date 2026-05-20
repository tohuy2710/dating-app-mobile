package com.example.dating.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.dating.ui.chat.ChatOptionsScreen
import com.example.dating.ui.chat.ChatScreen
import com.example.dating.ui.chat.ConversationScreen
import com.example.dating.ui.profile.ProfileScreen
import com.example.dating.ui.traditional.TraditionalMatchingHomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    initialMatchId: Int? = null
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    val hideBottomBar =
        currentRoute?.startsWith("conversation") == true ||
                currentRoute?.startsWith("chat_options") == true ||
                currentRoute == Screen.MatchProfile.route ||
                currentRoute == Screen.Settings.route ||
                currentRoute == Screen.EditInterests.route

    LaunchedEffect(initialMatchId) {
        if (initialMatchId != null) {
            navController.navigate(
                Screen.Conversation.createRoute(initialMatchId)
            )
        }
    }

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

                TraditionalMatchingHomeScreen(
                    onMatchNowClick = {
                        navController.navigate(
                            Screen.MatchProfile.route
                        )
                    }
                )
            }

            composable(Screen.Anonymous.route) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Anonymous matching coming soon"
                    )
                }
            }

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

            composable(Screen.MatchProfile.route) {

                ProfileScreen(
                    onBackClick = {
                        navController.popBackStack()
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

                if (matchId != null) {

                    ConversationScreen(
                        matchId = matchId,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onAvatarClick = {

                            navController.navigate(
                                Screen.ChatOptions.createRoute(
                                    matchId
                                )
                            )
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

                val matchId =
                    backStackEntry.arguments?.getInt("matchId")

                if (matchId != null) {

                    ChatOptionsScreen(
                        matchId = matchId,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}