package com.example.dating.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkSurface
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightSurface
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
    val showTutorial by viewModel.showTutorial.collectAsState()

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

    // BỌC TOÀN BỘ TRONG BOX
    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
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
                                    offsetX > 120f -> viewModel.likeUser(currentUser)
                                    offsetX < -120f -> viewModel.passUser(currentUser)
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
                        .statusBarsPadding() // FIX LỖI KÉO DÃN BUTTON BACK
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

        // POPUP HƯỚNG DẪN
        AnimatedVisibility(
            visible = showTutorial,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            LaunchedEffect(Unit) {
                delay(3000L) // Ẩn sau 3 giây
                viewModel.hideTutorialLocally()
            }

            TutorialPopup(
                onUnderstandClick = { viewModel.completeTutorial() }
            )
        }
    }
}

// UI EmptyState tuỳ chỉnh dành riêng cho màn hình này
@Composable
fun LikesReceivedEmptyState(
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) DarkText else LightText
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // NÚT BACK: Đã sửa lỗi căn lề và padding
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại",
                tint = textColor
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

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = ButtonDefaults.ContentPadding,
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(listOf(BrandPinkDark, BrandPink)),
                        shape = RoundedCornerShape(999.dp)
                    )
            ) {
                Text(
                    text = "Tải lại",
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TutorialPopup(onUnderstandClick: () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (isDarkTheme) DarkText else LightText

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .systemBarsPadding(),
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.08f)),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vuốt sang trái nếu muốn bỏ qua và vuốt sang phải nếu muốn kết nối.",
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onUnderstandClick,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = ButtonDefaults.ContentPadding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(BrandPinkDark, BrandPink)),
                        shape = RoundedCornerShape(999.dp)
                    )
            ) {
                Text(
                    text = "Đã rõ, không nhắc lại",
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}