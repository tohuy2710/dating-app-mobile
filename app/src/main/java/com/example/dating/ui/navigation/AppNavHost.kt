package com.example.dating.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dating.ui.chat.*
import com.example.dating.ui.profile.*
import com.example.dating.ui.traditional.TraditionalMatchProfileScreen
import com.example.dating.ui.traditional.TraditionalMatchingHomeScreen
import com.example.dating.ui.traditional.TraditionalMatchingViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideBottomBar =
        currentRoute?.startsWith("conversation") == true ||
                currentRoute?.startsWith("chat_options") == true ||
                currentRoute?.startsWith("match_profile") == true ||
                currentRoute == Screen.Settings.route ||
                currentRoute == Screen.EditInterests.route

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
                val matchingViewModel: TraditionalMatchingViewModel = viewModel(
                    factory = TraditionalMatchingViewModel.Factory
                )

                TraditionalMatchingHomeScreen(
                    viewModel = matchingViewModel,
                    onMatchNowClick = { user ->
                        navController.navigate(
                            Screen.MatchProfile.createRoute(user.userId)
                        )
                    }
                )
            }

            composable(Screen.Anonymous.route) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Anonymous matching coming soon")
                }
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
                route = Screen.MatchProfile.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId")
                val matchingViewModel: TraditionalMatchingViewModel = viewModel(
                    navController.getBackStackEntry(Screen.Traditional.route),
                    factory = TraditionalMatchingViewModel.Factory
                )
                val currentProfile by matchingViewModel.currentDiscoveryProfile.collectAsStateWithLifecycle()
                val currentTargetId by matchingViewModel.currentDiscoveryTargetId.collectAsStateWithLifecycle()
                val user = currentProfile?.takeIf { it.userId == currentTargetId || it.userId == userId }

                LaunchedEffect(userId, currentProfile, currentTargetId) {
                    if (user == null && currentProfile == null) {
                        matchingViewModel.loadDiscoveryProfiles(reset = false)
                    }
                }

                TraditionalMatchProfileScreen(
                    user = user,
                    targetUserId = currentTargetId ?: userId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onPassClick = {
                        matchingViewModel.passCurrentDiscoveryUser(currentTargetId ?: userId)
                    },
                    onMatchNowClick = {
                        matchingViewModel.likeCurrentDiscoveryUser(currentTargetId ?: userId)
                    },
                    onLikeClick = {
                        matchingViewModel.likeCurrentDiscoveryUser(currentTargetId ?: userId)
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.EditInterests.route) {
                InterestsSelectionScreen(onBackClick = { navController.popBackStack() })
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
                UserProfileScreen(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onEditInterestsClick = { navController.navigate(Screen.EditInterests.route) }
                )
            }
        }
    }
}
