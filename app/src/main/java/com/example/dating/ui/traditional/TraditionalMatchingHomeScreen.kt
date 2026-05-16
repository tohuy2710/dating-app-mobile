package com.example.dating.ui.traditional

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.dating.R
import com.example.dating.data.model.User
import com.example.dating.ui.theme.*
import java.time.LocalDate
import java.time.Period

@Composable
fun TraditionalMatchingHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: TraditionalMatchingViewModel = viewModel(factory = TraditionalMatchingViewModel.Factory),
    onMatchNowClick: (User) -> Unit = {}
) {
    val currentProfile by viewModel.currentDiscoveryProfile.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (currentProfile == null && uiState == MatchingUiState.Idle) {
            viewModel.loadDiscoveryProfiles(reset = true)
        }
    }

    TraditionalMatchingHomeScreenContent(
        modifier = modifier,
        currentProfile = currentProfile,
        uiState = uiState,
        onMatchNowClick = {
            currentProfile?.let(onMatchNowClick)
        },
        onPassClick = {
            viewModel.passCurrentDiscoveryUser()
        },
        onLikeClick = {
            viewModel.likeCurrentDiscoveryUser()
        }
    )
}

@Composable
fun TraditionalMatchingHomeScreenContent(
    modifier: Modifier = Modifier,
    currentProfile: User?,
    uiState: MatchingUiState,
    onMatchNowClick: () -> Unit = {},
    onPassClick: () -> Unit = {},
    onLikeClick: () -> Unit = {}
) {
    val profileAge = remember(currentProfile?.birthDate) {
        currentProfile?.birthDate?.let(::calculateAgeFromBirthDate)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Traditional Match",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find your wife",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedGradientBorderImage(
            imageUrl = currentProfile?.primaryPhoto?.imageUrl ?: currentProfile?.avatarUrl,
            name = currentProfile?.fullName,
            age = profileAge?.toString(),
            bio = currentProfile?.bio,
            isEmpty = currentProfile == null && uiState !is MatchingUiState.Loading
        )

        if (uiState is MatchingUiState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                color = BrandPink
            )
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        MatchNowButton(
            onClick = onMatchNowClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onPassClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Pass")
            }
            Button(
                onClick = onLikeClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPink)
            ) {
                Text(text = "Like")
            }
        }

        when (uiState) {
            is MatchingUiState.Success -> {
                Text(
                    text = uiState.message,
                    color = BrandPinkDark,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            is MatchingUiState.Error -> {
                Text(
                    text = uiState.message,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            else -> Unit
        }
    }
}

@Composable
private fun AnimatedGradientBorderImage(
    imageUrl: String?,
    name: String?,
    age: String?,
    bio: String?,
    isEmpty: Boolean
) {
    val infinite = rememberInfiniteTransition(label = "border")

    val progress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing)
        ),
        label = "progress"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val stroke = 6.dp
        val radius = 20.dp

        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()

        val cx = w / 2 + (w / 2) * kotlin.math.cos(2 * Math.PI * progress).toFloat()
        val cy = h / 2 + (h / 2) * kotlin.math.sin(2 * Math.PI * progress).toFloat()

        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                BrandPink,
                                BrandPinkDark,
                                Color(0xFFFF8AD8),
                                BrandPink
                            ),
                            center = Offset(cx, cy)
                        ),
                        cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
                        style = Stroke(stroke.toPx())
                    )
                }
                .padding(stroke)
                .clip(RoundedCornerShape(radius))
        ) {
            if (isEmpty) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color(0xFFF6F6F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No profiles available right now",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                    placeholder = painterResource(id = R.drawable.thl),
                    error = painterResource(id = R.drawable.thl)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.thl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            if (!isEmpty && name != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.78f))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "$name${if (age != null) ", $age" else ""}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (!bio.isNullOrBlank()) {
                            Text(
                                text = bio,
                                color = Color.White.copy(alpha = 0.92f),
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchNowButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = ButtonDefaults.ContentPadding,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(BrandPinkDark, BrandPink)
                ),
                shape = RoundedCornerShape(999.dp)
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Match Now",
                style = MaterialTheme.typography.titleMedium,
                color = White,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = White
            )
        }
    }
}

private fun calculateAgeFromBirthDate(birthDate: String): Int? {
    return runCatching {
        Period.between(LocalDate.parse(birthDate), LocalDate.now()).years
    }.getOrNull()
}

@Preview(showBackground = true)
@Composable
private fun PreviewScreen() {
    MarsPhotosTheme {
        Surface {
            TraditionalMatchingHomeScreenContent(
                currentProfile = User(
                    userId = 1,
                    fullName = "John Doe",
                    birthDate = "1995-01-01",
                    bio = "Just a guy looking for a match",
                    createdAt = "2023-01-01"
                ),
                uiState = MatchingUiState.Idle
            )
        }
    }
}
