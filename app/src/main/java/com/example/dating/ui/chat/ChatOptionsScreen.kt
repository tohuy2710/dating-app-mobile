package com.example.dating.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Pending
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
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightText
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.BrandPinkDark

import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatOptionsScreen(
    matchId: Int,
    currentUserId: Int?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = viewModel(
        factory = ConversationViewModelFactory(matchId)
    )
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkText else LightText
    
    when (val state = viewModel.conversationUiState) {
        ConversationUiState.Loading -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading...", color = textColor)
            }
        }
        is ConversationUiState.Success -> {
            ChatOptionsScreenContent(
                user = state.conversation.user,
                matchMode = state.conversation.matchMode,
                pendingUpgradeRequest = state.conversation.pendingUpgradeRequest,
                currentUserId = currentUserId,
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
                onRequestUpgradeClick = {
                    viewModel.requestUpgrade()
                },
                onRespondUpgradeClick = { status ->
                    viewModel.respondToUpgrade(status)
                },
                modifier = modifier
            )
        }
        is ConversationUiState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text("Error loading user", color = textColor)
            }
        }
        ConversationUiState.Idle -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(backgroundColor)
            )
        }
    }
}

@Composable
private fun ChatOptionsScreenContent(
    user: ChatUser,
    matchMode: String,
    pendingUpgradeRequest: MatchUpgradeRequest?,
    currentUserId: Int?,
    onBackClick: () -> Unit,
    onBlockClick: () -> Unit,
    onReportClick: () -> Unit,
    onUnmatchClick: () -> Unit,
    onRequestUpgradeClick: () -> Unit,
    onRespondUpgradeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkText else LightText
    val cardBackgroundColor = if (isDarkTheme) Color(0xFF333335) else Color(0xFFEEEEEE)
    val dividerColor = if (isDarkTheme) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)

    val displayUser = getDisplayUser(
        user = user,
        matchMode = matchMode
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
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
                    tint = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Avatar
        AsyncImage(
            model = displayUser.avatar,
            contentDescription = user.fullName,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tên người dùng
        Text(
            text = displayUser.fullName,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Card Container chứa các tuỳ chọn
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBackgroundColor)
                .padding(vertical = 12.dp)
        ) {
            if (matchMode == "anonymous" && currentUserId != null) {
                if (pendingUpgradeRequest == null) {
                    OptionItem(
                        icon = Icons.Outlined.LockOpen,
                        title = "Yêu cầu mở ẩn danh",
                        onClick = onRequestUpgradeClick
                    )
                    Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 24.dp))
                } else {
                    if (pendingUpgradeRequest.requesterId == currentUserId) {
                        OptionItem(
                            icon = Icons.Outlined.Pending,
                            title = "Đã yêu cầu mở. Đang chờ...",
                            onClick = { /* Không làm gì */ }
                        )
                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 24.dp))
                    } else {
                        // Nhận được yêu cầu từ đối phương
                        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                            Text(
                                text = "Đối phương muốn mở danh tính",
                                color = textColor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { onRespondUpgradeClick("accepted") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPink)
                                ) {
                                    Text("Đồng ý", color = Color.White)
                                }
                                OutlinedButton(
                                    onClick = { onRespondUpgradeClick("rejected") },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Từ chối", color = textColor)
                                }
                            }
                        }
                        Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 24.dp))
                    }
                }
            }

            OptionItem(icon = Icons.Outlined.Block, title = "Block", onClick = onBlockClick)
            Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 24.dp))

            OptionItem(icon = Icons.Default.Report, title = "Report", onClick = onReportClick)
            Divider(color = dividerColor, modifier = Modifier.padding(horizontal = 24.dp))

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
    val isDarkTheme = isSystemInDarkTheme()
    val textColor = if (isDarkTheme) Color.White else Color.Black
    
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
            tint = textColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}