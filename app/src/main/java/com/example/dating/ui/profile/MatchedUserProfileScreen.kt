package com.example.dating.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.BrandPink

@Composable
fun MatchedUserProfileScreen(
    userId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatchedUserProfileViewModel = viewModel(
        factory = MatchedUserProfileViewModelFactory(userId)
    )
) {
    val scrollState = rememberScrollState()
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        when (val state = uiState) {
            is MatchedProfileUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BrandPink // Thêm màu cho loader nếu muốn
                )
            }

            is MatchedProfileUiState.Error -> {
                EmptyState(
                    onRetry = { viewModel.loadUserProfile() },
                    onBackClick = onBackClick
                )
            }

            is MatchedProfileUiState.Success -> {
                val user = state.user

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Hero Image (KHÔNG có modifier vuốt)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        HeroImage(imageUrl = user.avatarUrl)

                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(16.dp)
                                .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ProfileCard(user)

                    Spacer(modifier = Modifier.height(16.dp))

                    val interests = user.preferences?.anonymousInterests?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
                    if (interests.isNotEmpty()) {
                        InterestChips(interests)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (user.photos.isNotEmpty()) {
                        GalleryGrid(user.photos)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}