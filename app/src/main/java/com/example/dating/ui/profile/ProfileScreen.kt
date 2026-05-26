package com.example.dating.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dating.R
import com.example.dating.data.model.User
import com.example.dating.data.model.UserPhoto
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkSecondaryText
import com.example.dating.ui.theme.DarkSurface
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightSecondaryText
import com.example.dating.ui.theme.LightSurface
import com.example.dating.ui.theme.LightText
import com.example.dating.ui.theme.SecondaryPurple
import com.example.dating.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Locale

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

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    if (currentUser == null) {
        EmptyState(
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

@Composable
fun HeroImage(imageUrl: String?) {

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
fun ProfileCard(user: User) {
    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (isDarkTheme) DarkText else LightText
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(surfaceColor)
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
                    text = formatBirthDate(user.birthDate),
                    color = secondaryTextColor,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = user.bio ?: "Chưa có giới thiệu",
                    color = textColor,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun InterestChips(interests: List<String>) {
    ChipRow(labels = interests)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipRow(labels: List<String>) {
    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(surfaceColor)
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
fun GalleryGrid(photos: List<UserPhoto>) {

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
fun EmptyState(
    onRetry: () -> Unit,
    onBackClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) DarkText else LightText
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Không có người dùng nào phù hợp",
                color = textColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onRetry) {
                Text("Tải lại")
            }
        }
    }
}

fun formatBirthDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Không rõ ngày sinh"

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        // Nếu parse lỗi, trả về nguyên gốc hoặc giá trị mặc định
        dateString
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}