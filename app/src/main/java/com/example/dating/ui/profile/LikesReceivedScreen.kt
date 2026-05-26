package com.example.dating.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightText
import com.example.dating.ui.theme.White

@Composable
fun LikesReceivedScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: LikesReceivedViewModel = viewModel(),
) {
    val users by viewModel.users.collectAsState()
    val index by viewModel.index.collectAsState()

    val currentUser = users.getOrNull(index)

    val scrollState = rememberScrollState()
    var offsetX by remember { mutableStateOf(0f) }

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    if (currentUser == null) {
        LikesReceivedEmptyState(
            onRetry = { viewModel.loadNextPage() },
            onBackClick = onBackClick
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
    ) {
        // HERO (ONLY SWIPE HERE)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(currentUser.userId) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX += dragAmount
                        },
                        onDragEnd = {
                            when {
                                offsetX > 250 -> viewModel.likeUser(currentUser)
                                offsetX < -250 -> viewModel.passUser(currentUser)
                            }
                            offsetX = 0f
                        }
                    )
                }
                .graphicsLayer {
                    translationX = offsetX
                    rotationZ = offsetX / 60
                }
        ) {
            HeroImage(currentUser.avatarUrl)

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        ProfileCard(currentUser)
        Spacer(modifier = Modifier.height(16.dp))
        InterestChips(
            currentUser.preferences
                ?.anonymousInterests
                ?.split(",")
                ?.filter { it.isNotBlank() }
                ?: emptyList()
        )
        Spacer(modifier = Modifier.height(16.dp))
        GalleryGrid(currentUser.photos)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// UI EmptyState tuỳ chỉnh dành riêng cho màn hình này
@Composable
fun LikesReceivedEmptyState(
    onRetry: () -> Unit,
    onBackClick: () -> Unit // Thêm tham số callback này
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) DarkText else LightText
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // NÚT BACK: Nằm ở góc trên cùng bên trái
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp) // padding top để tránh đè status bar nếu cần
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = textColor // Tự động đổi màu trắng/đen theo theme
            )
        }

        // NỘI DUNG CHÍNH: Nằm ở chính giữa màn hình
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Không có ai thích bạn",
                color = textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onRetry) {
                Text("Tải lại")
            }
        }
    }
}