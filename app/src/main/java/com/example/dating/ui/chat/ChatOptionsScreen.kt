package com.example.dating.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkText

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatOptionsScreen(
    matchId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = viewModel(
        factory = ConversationViewModelFactory(matchId)
    )
) {
    when (val state = viewModel.conversationUiState) {
        ConversationUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", color = DarkText)
            }
        }
        is ConversationUiState.Success -> {
            ChatOptionsScreenContent(
                user = state.conversation.user,
                onBackClick = onBackClick,
                onBlockClick = {
                    // TODO: Implement block functionality
                    onBackClick()
                },
                onReportClick = {
                    // TODO: Implement report functionality
                    onBackClick()
                },
                onUnmatchClick = {
                    viewModel.deleteMatch()
                    onBackClick()
                },
                modifier = modifier
            )
        }
        is ConversationUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkBackground),
                contentAlignment = Alignment.Center
            ) {
                Text("Error loading user", color = DarkText)
            }
        }
        ConversationUiState.Idle -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(DarkBackground)
            )
        }
    }
}

@Composable
private fun ChatOptionsScreenContent(
    user: ChatUser,
    onBackClick: () -> Unit,
    onBlockClick: () -> Unit,
    onReportClick: () -> Unit,
    onUnmatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row Header chứa nút back giống hệt style của ConversationHeader
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkText
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar
        AsyncImage(
            model = user.getDisplayAvatarUrl(),
            contentDescription = user.fullName,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tên người dùng
        Text(
            text = user.fullName,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Card Container chứa các tuỳ chọn
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF333335))
                .padding(vertical = 12.dp)
        ) {
            OptionItem(icon = Icons.Outlined.Block, title = "Block", onClick = onBlockClick)
            Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 24.dp))

            OptionItem(icon = Icons.Default.Report, title = "Report", onClick = onReportClick)
            Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 24.dp))

            OptionItem(icon = Icons.Default.Delete, title = "Unmatch", onClick = onUnmatchClick)
        }
    }
}

@Composable
private fun OptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}