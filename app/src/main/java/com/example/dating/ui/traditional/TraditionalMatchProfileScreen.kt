package com.example.dating.ui.traditional

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dating.R
import com.example.dating.data.model.User
import com.example.dating.data.model.UserPhoto
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BorderGray
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.Gray700
import com.example.dating.ui.theme.MarsPhotosTheme
import com.example.dating.ui.theme.SecondaryPurple
import com.example.dating.ui.theme.White
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TraditionalMatchProfileScreen(
    user: User?,
    targetUserId: Int? = user?.userId,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onPassClick: () -> Unit = {},
    onMatchNowClick: () -> Unit = {},
    onLikeClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val age = remember(user?.birthDate) { user?.birthDate?.let(::calculateAgeFromBirthDate) }
    val displayPhoto = user?.primaryPhoto?.imageUrl ?: user?.avatarUrl
    val title = buildString {
        append(user?.fullName.orEmpty().ifBlank { "Unknown user" })
        age?.let { append(", $it") }
    }
    val subtitle = listOfNotNull(
        user?.gender?.toGenderLabel().takeUnless { it == "Not provided" },
        user?.email?.takeIf { it.isNotBlank() }
    ).joinToString(" • ").ifBlank { "Traditional match profile" }
    val interestLabels = remember(user) {
        listOfNotNull(
            user?.gender?.toGenderLabel().takeUnless { it == "Not provided" },
            age?.let { "$it tuổi" },
            user?.email?.substringBefore('@')?.takeIf { it.isNotBlank() },
            user?.createdAt?.toReadableDate()?.let { "Tham gia $it" }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TraditionalHeroImage(photoUrl = displayPhoto)

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = White
                )
            }

            TraditionalProfileActions(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 36.dp),
                onPassClick = onPassClick,
                onMatchNowClick = onMatchNowClick,
                onLikeClick = onLikeClick
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        TraditionalProfileCard(
            title = title,
            subtitle = subtitle,
            bio = user?.bio,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (interestLabels.isNotEmpty()) {
            TraditionalInterestChips(
                labels = interestLabels,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        TraditionalPhotoGrid(
            user = user,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TraditionalProfileFacts(
            user = user,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onMatchNowClick,
            shape = RoundedCornerShape(999.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandPink),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Match Now",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )
        }
    }
}

@Composable
private fun TraditionalHeroImage(photoUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Color(0xFFF2D0D8))
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.thl),
            error = androidx.compose.ui.res.painterResource(id = R.drawable.thl),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TraditionalProfileActions(
    modifier: Modifier = Modifier,
    onPassClick: () -> Unit,
    onMatchNowClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TraditionalFloatingActionButton(
            onClick = onPassClick,
            size = 56.dp,
            solidColor = SecondaryPurple,
            icon = Icons.Default.Close,
            iconTint = White,
            iconModifier = Modifier.drawWithContent {
                val contentDrawScope = this
                drawContent()
                translate(left = 0.6f, top = 0f) {
                    contentDrawScope.drawContent()
                }
            }
        )
        TraditionalFloatingActionButton(
            onClick = onMatchNowClick,
            size = 72.dp,
            background = Brush.linearGradient(listOf(BrandPinkDark, BrandPink)),
            icon = Icons.Default.Send,
            iconTint = White,
            iconModifier = Modifier
                .graphicsLayer {
                    rotationZ = -45f
                    transformOrigin = TransformOrigin.Center
                }
                .offset(x = 4.dp)
        )
        TraditionalFloatingActionButton(
            onClick = onLikeClick,
            size = 56.dp,
            solidColor = SecondaryPurple,
            icon = Icons.Default.Favorite,
            iconTint = White
        )
    }
}

@Composable
private fun TraditionalFloatingActionButton(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
    background: Brush? = null,
    solidColor: Color? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    iconModifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 1.08f else 1f, label = "traditionalFabScale")

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
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        val released = tryAwaitRelease()
                        pressed = false
                        if (released) {
                            onClick()
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (background != null) {
            Box(modifier = Modifier.matchParentSize().background(background))
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(size * 0.8f).then(iconModifier)
        )
    }
}

@Composable
private fun TraditionalProfileCard(
    title: String,
    subtitle: String,
    bio: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.firstOrNull()?.uppercase() ?: "?",
                    color = BrandPink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = BrandPink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Black900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = SecondaryPurple)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Traditional matching",
                color = Gray700,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = BorderGray,
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = bio?.ifBlank { "Chưa có mô tả" } ?: "Chưa có mô tả",
                modifier = Modifier.padding(16.dp),
                color = Gray700,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TraditionalInterestChips(
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        labels.forEach { label ->
            Surface(
                color = Color(0xFFF1F1F1),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.border(1.dp, Color(0xFFE5E5E5), RoundedCornerShape(999.dp))
            ) {
                Text(
                    text = label,
                    color = Gray700,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
private fun TraditionalPhotoGrid(
    user: User?,
    modifier: Modifier = Modifier
) {
    val photoUrls = remember(user) {
        buildList {
            user?.primaryPhoto?.imageUrl?.let(::add)
            user?.avatarUrl?.takeIf { it != user?.primaryPhoto?.imageUrl }?.let(::add)
        }.ifEmpty { listOf<String?>(null, null, null, null) }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        photoUrls.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { url ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF2D0D8))
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.thl),
                            error = androidx.compose.ui.res.painterResource(id = R.drawable.thl),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TraditionalProfileFacts(
    user: User?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TraditionalQACard(
            question = "Email",
            answer = user?.email?.ifBlank { "Chưa cung cấp" } ?: "Chưa cung cấp"
        )
        TraditionalQACard(
            question = "Ngày sinh",
            answer = user?.birthDate?.ifBlank { "Chưa cung cấp" } ?: "Chưa cung cấp"
        )
        TraditionalQACard(
            question = "Ngày tham gia",
            answer = user?.createdAt?.toReadableDate() ?: "Chưa cung cấp"
        )
    }
}

@Composable
private fun TraditionalQACard(question: String, answer: String) {
    Surface(
        color = White,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = question,
                color = BrandPink,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = answer,
                color = Black900,
                fontSize = 14.sp
            )
        }
    }
}

private fun String?.toGenderLabel(): String {
    return when (this?.uppercase()) {
        "M" -> "Nam"
        "F" -> "Nữ"
        "O" -> "Khác"
        else -> this?.ifBlank { "Not provided" } ?: "Not provided"
    }
}

private fun calculateAgeFromBirthDate(birthDate: String): Int? {
    return runCatching {
        Period.between(LocalDate.parse(birthDate), LocalDate.now()).years
    }.getOrNull()
}

private fun String.toReadableDate(): String {
    return runCatching {
        val instant = java.time.Instant.parse(this)
        DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneOffset.UTC).format(instant)
    }.recoverCatching {
        DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.parse(this))
    }.getOrElse { this }
}

@Preview(showBackground = true)
@Composable
private fun TraditionalMatchProfileScreenPreview() {
    MarsPhotosTheme {
        TraditionalMatchProfileScreen(
            user = User(
                userId = 7,
                email = "linh@example.com",
                fullName = "Trần Hà Linh",
                birthDate = "2006-01-01",
                gender = "F",
                bio = "Thích cà phê, âm nhạc và những cuộc trò chuyện tử tế.",
                avatarUrl = null,
                primaryPhoto = UserPhoto(
                    photoId = 99,
                    imageUrl = "https://example.com/photo.jpg",
                    isPrimary = true
                ),
                createdAt = "2026-01-01T00:00:00Z"
            )
        )
    }
}
