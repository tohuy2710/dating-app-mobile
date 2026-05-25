package com.example.dating.ui.anonymous

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dating.R
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
import com.example.dating.ui.theme.MarsPhotosTheme
import com.example.dating.ui.theme.White
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnonymousMatchingScreen(
    modifier: Modifier = Modifier,
    viewModel: AnonymousViewModel = viewModel(),
    onNavigateToConversation: (Int) -> Unit = {}
) {

    val uiState = viewModel.anonymousUiState
    val remainingSeconds = viewModel.remainingSeconds

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (isDarkTheme) DarkText else LightText
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(56.dp))

        AnimatedAnonymousCard()

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Kết nối ẩn danh",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnonymousFeatureRow()

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.startAnonymousMatching()
            },
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            BrandPinkDark,
                            BrandPink
                        )
                    ),
                    shape = RoundedCornerShape(999.dp)
                )
        ) {

            Text(
                text = "Bắt đầu",
                style = MaterialTheme.typography.titleMedium,
                color = White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    /**
     * SEARCHING / QUEUED
     */
    if (
        uiState is AnonymousUiState.Searching ||
        uiState is AnonymousUiState.Queued
    ) {

        AlertDialog(
            onDismissRequest = {},

            confirmButton = {

                Button(
                    onClick = {
                        viewModel.cancelMatching()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPink
                    ),
                    shape = RoundedCornerShape(999.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = White
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Hủy",
                        color = White
                    )
                }
            },

            title = {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    RotatingAnonymousIcon()

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Đang tìm kiếm...",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            },

            text = {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "Đang tìm người phù hợp với bạn",
                        style = MaterialTheme.typography.bodyMedium,
                        color = secondaryTextColor
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    CircularProgressIndicator(
                        color = BrandPink
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "${remainingSeconds}s",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = BrandPink
                    )
                }
            },

            shape = RoundedCornerShape(28.dp),
            containerColor = surfaceColor
        )
    }

    /**
     * MATCHED
     */
    if (uiState is AnonymousUiState.Matched) {

        val match = uiState.match

        AlertDialog(
            onDismissRequest = {},

            confirmButton = {

                Button(
                    onClick = {

                        match?.matchId?.let { matchId ->

                            viewModel.resetState()

                            onNavigateToConversation(matchId)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPink
                    ),
                    shape = RoundedCornerShape(999.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = White
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Nhắn tin ngay",
                        color = White
                    )
                }
            },

            title = {

                Text(
                    text = "Đã tìm thấy người phù hợp 🎉",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },

            text = {

                Column {

                    Text(
                        text = "Hai bạn có nhiều sở thích tương đồng.",
                        color = secondaryTextColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Điểm tương thích: ${uiState.matchScore ?: 0}",
                        fontWeight = FontWeight.Bold,
                        color = BrandPink
                    )
                }
            },

            shape = RoundedCornerShape(28.dp),
            containerColor = surfaceColor
        )
    }

    /**
     * TIMEOUT
     */
    if (uiState is AnonymousUiState.Timeout) {

        AlertDialog(
            onDismissRequest = {
                viewModel.resetState()
            },

            confirmButton = {

                Button(
                    onClick = {
                        viewModel.resetState()
                    }
                ) {

                    Text("OK")
                }
            },

            title = {
                Text(
                    text = "Không tìm thấy ai",
                    color = textColor
                )
            },

            text = {
                Text(
                    text = "Hiện chưa có người phù hợp. Hãy thử lại sau.",
                    color = secondaryTextColor
                )
            },

            containerColor = surfaceColor
        )
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun AnimatedAnonymousCard() {

    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (isDarkTheme) DarkText else LightText

    val infinite = rememberInfiniteTransition(label = "anonymous")

    val progress by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2600,
                easing = LinearEasing
            )
        ),
        label = "progress"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {

        val stroke = 6.dp
        val radius = 24.dp

        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()

        val cx =
            w / 2 + (w / 2) * cos(2 * Math.PI * progress).toFloat()

        val cy =
            h / 2 + (h / 2) * sin(2 * Math.PI * progress).toFloat()

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
                        cornerRadius = CornerRadius(
                            radius.toPx(),
                            radius.toPx()
                        ),
                        style = Stroke(stroke.toPx())
                    )
                }
                .padding(stroke)
                .clip(RoundedCornerShape(radius))
                .background(surfaceColor),
            contentAlignment = Alignment.Center
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    BrandPinkDark,
                                    BrandPink
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.anonymous_avatar),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Ẩn danh tuyệt đối",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}

@Composable
private fun AnonymousFeatureRow() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        FeatureItem(
            iconContent = {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = "Riêng tư"
        )

        FeatureItem(
            iconContent = {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = BrandPink,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = "Cùng sở thích"
        )

        FeatureItem(
            iconContent = {
                Image(
                    painter = painterResource(id = R.drawable.anonymous_avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
            },
            title = "Ẩn danh"
        )
    }
}

@Composable
private fun FeatureItem(
    iconContent: @Composable () -> Unit,
    title: String
) {
    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(surfaceColor),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = secondaryTextColor
        )
    }
}

@Composable
private fun RotatingAnonymousIcon() {
    val isDarkTheme = isSystemInDarkTheme()
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface

    val infinite = rememberInfiniteTransition(label = "rotate")

    val rotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = LinearEasing
            )
        ),
        label = "rotation"
    )

    Box(
        contentAlignment = Alignment.Center
    ) {

        Surface(
            modifier = Modifier.size(84.dp),
            shape = CircleShape,
            color = surfaceColor
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.anonymous_avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .graphicsLayer { rotationZ = rotation } // Áp dụng hiệu ứng xoay tròn
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAnonymousScreen() {

    MarsPhotosTheme {

        Surface {
            AnonymousMatchingScreen()
        }
    }
}