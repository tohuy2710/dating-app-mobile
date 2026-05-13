package com.example.dating.ui.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Register : Screen("register")

    object Traditional : Screen("traditional")

    object Anonymous : Screen("anonymous")

    object Chat : Screen("chat")

    object Profile : Screen("profile")
    
    object MatchProfile : Screen("match_profile")

    object Settings : Screen("settings")

    object EditInterests : Screen("edit_interests")

    object Conversation : Screen("conversation/{matchId}") {

        fun createRoute(matchId: Int) =
            "conversation/$matchId"
    }

    object ChatOptions : Screen("chat_options/{matchId}") {
        fun createRoute(matchId: Int) = "chat_options/$matchId"
    }
}
