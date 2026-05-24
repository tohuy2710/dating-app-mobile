package com.example.dating.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.dating.R
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.Gray700
import com.example.dating.ui.theme.SecondaryPurple
import com.example.dating.ui.theme.White
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dating.data.model.User
import com.example.dating.data.model.UserPhoto

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    viewModel: DiscoveryViewModel = viewModel(),
) {
    val users by viewModel.users.collectAsState()
    val index by viewModel.index.collectAsState()

    val currentUser = users.getOrNull(index)

    val scrollState = rememberScrollState()
    var offsetX by remember { mutableStateOf(0f) }

    if (currentUser == null) {
        EmptyState(onRetry = { viewModel.loadNextPage() })
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
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

@Composable
private fun HeroImage(imageUrl: String?) {

    AsyncImage(
        model = imageUrl.takeIf { !it.isNullOrBlank() } ?: R.drawable.default_avatar,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
    )
}

@Composable
private fun ActionButtonsRow(
    modifier: Modifier = Modifier,
    onPass: () -> Unit,
    onLike: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        FloatingActionButton(
            size = 56.dp,
            solidColor = SecondaryPurple,
            icon = Icons.Default.Close,
            iconTint = White,
            onClick = onPass
        )

        FloatingActionButton(
            size = 56.dp,
            solidColor = SecondaryPurple,
            icon = Icons.Default.Favorite,
            iconTint = White,
            onClick = onLike
        )
    }
}

@Composable
private fun FloatingActionButton(
    size: Dp,
    background: Brush? = null,
    solidColor: Color? = null,
    icon: ImageVector,
    iconTint: Color,
    iconModifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.08f else 1f,
        label = ""
    )

    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(solidColor ?: Color.Transparent)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        haptic.performHapticFeedback(
                            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
                        )

                        val released = tryAwaitRelease()

                        pressed = false

                        if (released) onClick()
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (background != null) {
            Box(
                Modifier
                    .matchParentSize()
                    .background(background)
            )
        }

        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(size * 0.8f)
                .then(iconModifier)
        )
    }
}

@Composable
private fun ProfileCard(user: User) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = "${user.fullName}",
                    color = BrandPink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user.birthDate ?: "Không rõ ngày sinh",
                    color = Gray700,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = user.bio ?: "Chưa có giới thiệu",
                    color = Black900,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun InterestChips(interests: List<String>) {
    ChipRow(labels = interests)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipRow(labels: List<String>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .padding(16.dp)
    ) {

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            labels.forEach { label ->

                Surface(
                    color = BrandPink.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(999.dp),
                    modifier = Modifier.border(
                        1.dp,
                        BrandPink.copy(alpha = 0.6f),
                        RoundedCornerShape(999.dp)
                    )
                ) {
                    Text(
                        text = label,
                        color = BrandPinkDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryGrid(photos: List<UserPhoto>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        photos.chunked(2).forEach { row ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                row.forEach { photo ->

                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Không có người dùng nào phù hợp")

            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.material3.Button(onClick = onRetry) {
                Text("Tải lại")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
