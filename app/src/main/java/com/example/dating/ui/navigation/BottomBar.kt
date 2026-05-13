package com.example.dating.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.Gray300

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

    val selectedGradient = Brush.verticalGradient(
        colors = listOf(BrandPink, BrandPinkDark)
    )

    Column {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
        
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
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
                        val iconModifier = Modifier
                            .let { 
                                if (screen == Screen.Chat) it.rotate(-45f) else it 
                            }
                            .then(
                                if (selected) {
                                    Modifier
                                        .graphicsLayer(alpha = 0.99f)
                                        .drawWithCache {
                                            onDrawWithContent {
                                                drawContent()
                                                drawRect(selectedGradient, blendMode = BlendMode.SrcAtop)
                                            }
                                        }
                                } else Modifier
                            )

                        Icon(
                            imageVector = when (screen) {
                                Screen.Traditional -> Icons.Default.Style
                                Screen.Anonymous -> Icons.Default.Favorite
                                Screen.Chat -> Icons.AutoMirrored.Filled.Send
                                Screen.Profile -> Icons.Default.Person
                                else -> Icons.Default.Person
                            },
                            contentDescription = screen.route,
                            tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = iconModifier
                        )
                    },
                    label = null,
                    alwaysShowLabel = false,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
