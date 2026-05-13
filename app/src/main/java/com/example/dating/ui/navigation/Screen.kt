package com.example.dating.ui.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Register : Screen("register")

    object Preferences : Screen("preferences")

    object Traditional : Screen("traditional")

    object Anonymous : Screen("anonymous")

    object Chat : Screen("chat")

    object Profile : Screen("profile")

    object Conversation : Screen("conversation/{matchId}") {

        fun createRoute(matchId: Int) =
            "conversation/$matchId"
    }

    object ChatOptions : Screen("chat_options/{matchId}") {
        fun createRoute(matchId: Int) = "chat_options/$matchId"
    }
}