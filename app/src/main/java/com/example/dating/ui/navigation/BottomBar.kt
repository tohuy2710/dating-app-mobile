package com.example.dating.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavController
) {
    val items = listOf(
        Screen.Traditional,
        Screen.Anonymous,
        Screen.Chat,
        Screen.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {

        items.forEach { screen ->

            val selected = currentRoute == screen.route

            NavigationBarItem(
                selected = selected,
                onClick = {

                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {

                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Traditional -> Icons.Default.Favorite
                            Screen.Anonymous -> Icons.Default.Person
                            Screen.Chat -> Icons.Default.Chat
                            Screen.Profile -> Icons.Default.Person
                            else -> Icons.Default.Person
                        },
                        contentDescription = screen.route
                    )
                },
                label = {
                    Text(
                        text = when (screen) {
                            Screen.Traditional -> "Match"
                            Screen.Anonymous -> "Anonymous"
                            Screen.Chat -> "Chat"
                            Screen.Profile -> "Profile"
                            else -> ""
                        }
                    )
                }
            )
        }
    }
}