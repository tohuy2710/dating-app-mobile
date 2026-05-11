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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.dating.R
import com.example.dating.ui.theme.*

@Composable
fun TraditionalMatchingHomeScreen(
    modifier: Modifier = Modifier,
    onMatchNowClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        AnimatedGradientBorderImage()

        Spacer(modifier = Modifier.height(24.dp))

        MatchNowButton(onMatchNowClick)

        Spacer(modifier = Modifier.height(34.dp))

        ActionRow()
    }
}

@Composable
private fun AnimatedGradientBorderImage() {
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

        // Tâm gradient chạy vòng tròn quanh viền (KHÔNG rotate canvas)
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
            Image(
                painter = painterResource(id = R.drawable.thl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(radius))
            )
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

@Composable
private fun ActionRow() {
    val actionItems = listOf(
        Icons.Default.Favorite to "đã thích bạn",
        Icons.Default.Star to "nổi bật",
        Icons.Default.Email to "tin nhắn",
        Icons.Default.Star to "xem lại"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(
            24.dp,
            Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        actionItems.forEach { (icon, label) ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7561FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF818181)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewScreen() {
    MarsPhotosTheme {
        Surface {
            TraditionalMatchingHomeScreen()
        }
    }
}