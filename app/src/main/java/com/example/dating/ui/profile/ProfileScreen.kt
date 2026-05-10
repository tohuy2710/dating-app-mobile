package com.example.dating.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.dating.R
import com.example.dating.ui.theme.Black900
import com.example.dating.ui.theme.BorderGray
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark
import com.example.dating.ui.theme.Gray500
import com.example.dating.ui.theme.Gray700
import com.example.dating.ui.theme.SecondaryPurple
import com.example.dating.ui.theme.White

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            HeroImage()
            ActionButtonsRow(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 36.dp)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        ProfileCard()

        Spacer(modifier = Modifier.height(16.dp))

        InterestChips()

        Spacer(modifier = Modifier.height(16.dp))

        GalleryGrid()

        Spacer(modifier = Modifier.height(16.dp))

        QACards()
    }
}

@Composable
private fun HeroImage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Color(0xFFF2D0D8))
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
        )
    }
}

@Composable
private fun ActionButtonsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FloatingActionButton(
            size = 56.dp,
            solidColor = SecondaryPurple,
            icon = Icons.Default.Close,
            iconTint = White
        )
        FloatingActionButton(
            size = 72.dp,
            background = Brush.linearGradient(listOf(BrandPinkDark, BrandPink)),
            icon = Icons.Default.Close,
            iconTint = White
        )
        FloatingActionButton(
            size = 56.dp,
            solidColor = SecondaryPurple,
            icon = Icons.Default.Favorite,
            iconTint = White
        )
    }
}

@Composable
private fun FloatingActionButton(
    size: androidx.compose.ui.unit.Dp,
    background: Brush? = null,
    solidColor: Color? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color
) {
    val haptic = LocalHapticFeedback.current
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 1.08f else 1f, label = "fabScale")

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
                        tryAwaitRelease()
                        pressed = false
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (background != null) {
            Box(modifier = Modifier.matchParentSize().background(background))
        }
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(size * 0.42f))
    }
}

@Composable
private fun ProfileCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
                Text(text = "ĐH", color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Shi Zzu KKKa, 2005",
                    color = BrandPink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Đại học Quốc gia Hà Nội",
                    color = Black900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = SecondaryPurple)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Cầu Giấy",
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
                text = "Kích thước cơ bản cho Android thường là 360x576, 360x640 hoặc 360x720 (tỉ lệ 16:10, 16:9 hoặc 2:1). Vì vậy, tất cả các số đo trong hướng dẫn Material Design đều dựa trên độ phân giải này.",
                modifier = Modifier.padding(16.dp),
                color = Gray700,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun InterestChips() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ChipRow(listOf("Thanh Hóa", "Ăn rau má", "Học androidddd"))
        ChipRow(listOf("Phó đường tàu", "Đồ thơm mỡ ai đó 123666"))
    }
}

@Composable
private fun ChipRow(labels: List<String>) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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
private fun GalleryGrid() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GalleryTile(modifier = Modifier.weight(1f))
            GalleryTile(modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            GalleryTile(modifier = Modifier.weight(1f))
            GalleryTile(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GalleryTile(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF2D0D8))
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.loading_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun QACards() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QACard(
            question = "Gu cua em la j?",
            answer = "ng thanh hoa"
        )
        QACard(
            question = "Anh va bo em roi xuong ne em cua ai tre?",
            answer = "Cuu ca 2"
        )
    }
}

@Composable
private fun QACard(question: String, answer: String) {
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