/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dating.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dating.ui.profile.ProfileScreen
import com.example.dating.ui.traditional.TraditionalMatchingHomeScreen
import com.example.dating.ui.traditional.TraditionalMatchingHomeScreen

private object AppRoute {
    const val TraditionalMatchingHome = "traditional_matching_home"
    const val Profile = "profile"
}

@Composable
fun AppRoot(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoute.TraditionalMatchingHome,
        modifier = modifier
    ) {
        composable(AppRoute.TraditionalMatchingHome) {
            TraditionalMatchingHomeScreen(
                onMatchNowClick = { navController.navigate(AppRoute.Profile) }
            )
        }
        composable(AppRoute.Profile) {
            ProfileScreen()
        }
    }
}
