package com.example.dating.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.dating.ui.auth.AuthViewModel
import com.example.dating.ui.chat.ChatOptionsScreen
import com.example.dating.ui.chat.ChatScreen
import com.example.dating.ui.chat.ConversationScreen
import com.example.dating.ui.profile.InterestsSelectionScreen
import com.example.dating.ui.profile.ProfileScreen
import com.example.dating.ui.profile.ProfileViewModel
import com.example.dating.ui.profile.SettingsScreen
import com.example.dating.ui.profile.UserProfileScreen
import com.example.dating.ui.traditional.TraditionalMatchingHomeScreen
import com.example.dating.ui.anonymous.AnonymousMatchingScreen
import com.example.dating.ui.chat.ConversationViewModel
import com.example.dating.ui.profile.LikesReceivedScreen
import com.example.dating.ui.profile.LikesSentScreen
import com.example.dating.ui.profile.MatchedUserProfileScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    initialMatchId: Int? = null,
    onMatchIdHandled: () -> Unit = {},
    onLogout: () -> Unit,
    currentUserId: Int?
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    val hideBottomBar =
        currentRoute?.startsWith("conversation") == true ||
                currentRoute?.startsWith("chat_options") == true ||
                currentRoute?.startsWith("matched_profile") == true ||
                currentRoute == Screen.MatchProfile.route ||
                currentRoute == Screen.Settings.route ||
                currentRoute == Screen.EditInterests.route ||
                currentRoute == Screen.LikesReceived.route ||
                currentRoute == Screen.LikesSent.route

    var selectedInterests by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    LaunchedEffect(initialMatchId) {
        if (initialMatchId != null) {
            navController.navigate("conversation/$initialMatchId") {
                launchSingleTop = true
            }
            onMatchIdHandled()
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
                    },
                    onLikesReceivedClick = {
                        navController.navigate("likes_received")
                    },
                    onLikesSentClick = {
                        navController.navigate("likes_sent")
                    }
                )
            }

            composable(Screen.LikesReceived.route) {

                LikesReceivedScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onNavigateToConversation = { matchId ->
                        navController.navigate(
                            Screen.Conversation.createRoute(matchId)
                        )
                    }
                )
            }

            composable(Screen.LikesSent.route) {
                LikesSentScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Anonymous.route) {

                AnonymousMatchingScreen(
                    onNavigateToConversation = { matchId ->
                        navController.navigate(
                            Screen.Conversation.createRoute(matchId)
                        )
                    }
                )
            }

            composable(Screen.Chat.route) {

                ChatScreen(
                    currentUserId = currentUserId,
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
                    },
                    onNavigateToConversation = { matchId ->
                        navController.navigate(
                            Screen.Conversation.createRoute(matchId)
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

                if (matchId != null) {

                    ConversationScreen(
                        currentUserId = currentUserId,
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

                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Conversation.route)
                    }

                    val sharedViewModel: ConversationViewModel = viewModel(parentEntry)

                    ChatOptionsScreen(
                        matchId = matchId,
                        currentUserId = currentUserId,
                        viewModel = sharedViewModel,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onAvatarClick = { userId ->
                            navController.navigate("matched_profile/$userId")
                        },
                        onUnmatchSuccess = {
                            navController.popBackStack(Screen.Chat.route, inclusive = false)
                        },
                    )
                }
            }

            composable(
                route = "matched_profile/{userId}",
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId")

                if (userId != null) {
                    MatchedUserProfileScreen(
                        userId = userId,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = {
                        onLogout()
                    }
                )
            }

            composable(Screen.EditInterests.route) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(
                        Screen.Profile.route
                    )
                }

                val profileViewModel: ProfileViewModel =
                    viewModel(parentEntry)

                InterestsSelectionScreen(

                    initialSelectedInterests =
                        selectedInterests.toSet(),

                    onBackClick = {
                        navController.popBackStack()
                    },

                    onSelectionDone = { interests ->

                        profileViewModel.updateAnonymousInterests(
                            interests.toList()
                        )

                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Profile.route) {
                UserProfileScreen(
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onEditInterestsClick = { interests ->
                        selectedInterests = interests
                        navController.navigate(
                            Screen.EditInterests.route
                        )
                    }
                )
            }
        }
    }
}