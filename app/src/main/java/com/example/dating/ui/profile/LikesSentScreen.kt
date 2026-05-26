package com.example.dating.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.dating.data.model.User
import com.example.dating.ui.theme.*
import kotlin.math.abs

@Composable
fun LikesSentScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: LikesSentViewModel = viewModel(),
) {
    val users by viewModel.users.collectAsState()
    val index by viewModel.index.collectAsState()

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    if (users.isEmpty()) {
        LikesSentEmptyState(
            onRetry = { viewModel.loadNextPage() },
            onBackClick = onBackClick
        )
        return
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        var offsetX by remember { mutableStateOf(0f) }
        val maxDragDistance = 600f
        val dragProgress = (abs(offsetX) / maxDragDistance).coerceIn(0f, 1f)

        // TÍNH TOÁN CẢ NEXT INDEX VÀ PREV INDEX ĐỂ TẠO VÒNG LẶP
        val nextIndex = if (index + 1 >= users.size) 0 else index + 1
        val prevIndex = if (index - 1 < 0) users.size - 1 else index - 1

        // QUAN TRỌNG: Hiển thị thẻ ẩn bên dưới khớp với hướng tay vuốt
        // - Kéo sang trái (offsetX < 0) -> Cho xem trước Next User
        // - Kéo sang phải (offsetX > 0) -> Cho xem trước Previous User
        val backgroundIndex = if (offsetX < 0) nextIndex else prevIndex
        val backgroundUser = users.getOrNull(backgroundIndex)

        // 1. CARD NẰM DƯỚI (Background)
        if (backgroundUser != null) {
            val backgroundScale = 0.95f + (dragProgress * 0.05f)
            val backgroundAlpha = 0.6f + (dragProgress * 0.4f)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = backgroundScale
                        scaleY = backgroundScale
                        alpha = backgroundAlpha
                    }
            ) {
                CardContent(user = backgroundUser, onBackClick = onBackClick)
            }
        }

        // 2. CARD HIỆN TẠI (Nằm trên cùng)
        val currentUser = users[index]
        val currentScale by animateFloatAsState(targetValue = 1f - (dragProgress * 0.08f), label = "scale")
        val currentAlpha by animateFloatAsState(targetValue = 1f - (dragProgress * 0.3f), label = "alpha")

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(currentUser.userId, users.size) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            // Cho phép tự do kéo sang bất kỳ hướng nào không giới hạn
                            offsetX += dragAmount
                        },
                        onDragEnd = {
                            if (offsetX < -280) {
                                // Kéo sang trái mạnh -> Tiếp tục
                                viewModel.nextUser()
                            } else if (offsetX > 280) {
                                // Kéo sang phải mạnh -> Lùi lại
                                viewModel.previousUser()
                            }
                            // Reset về giữa
                            offsetX = 0f
                        }
                    )
                }
                .graphicsLayer {
                    translationX = offsetX
                    scaleX = currentScale
                    scaleY = currentScale
                    alpha = currentAlpha
                }
        ) {
            CardContent(user = currentUser, onBackClick = onBackClick)
        }
    }
}

@Composable
private fun CardContent(
    user: User,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isDarkTheme = isSystemInDarkTheme()
    val cardColor = if (isDarkTheme) DarkSurface else LightSurface

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                HeroImage(imageUrl = user.avatarUrl)

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            ProfileCard(user = user)

            Spacer(modifier = Modifier.height(12.dp))
            InterestChips(
                interests = user.preferences?.anonymousInterests
                    ?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
            )

            Spacer(modifier = Modifier.height(12.dp))
            GalleryGrid(photos = user.photos ?: emptyList())

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun LikesSentEmptyState(onRetry: () -> Unit, onBackClick: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) DarkText else LightText
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = textColor,
                contentDescription = "Quay lại"
            )
        }
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Bạn chưa thích ai hoặc danh sách trống", color = textColor)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onRetry) {
                Text("Tải lại")
            }
        }
    }
}