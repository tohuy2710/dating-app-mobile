/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dating.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.isSystemInDarkTheme
import coil.compose.AsyncImage
import com.example.dating.ui.theme.BrandPink
import com.example.dating.ui.theme.ChatBubblePink
import com.example.dating.ui.theme.ChatBubblePurple
import com.example.dating.ui.theme.ChatBubblePinkLight
import com.example.dating.ui.theme.ChatBubblePurpleLight
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkSecondaryText
import com.example.dating.ui.theme.DarkSurface
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightSecondaryText
import com.example.dating.ui.theme.LightSurface
import com.example.dating.ui.theme.LightText
import com.example.dating.ui.theme.Gray300
import java.text.SimpleDateFormat
import java.util.Locale
import java.net.URLDecoder

/**
 * Composable for a single message bubble in the conversation.
 */
@Composable
fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val bubbleColor = if (isCurrentUser) {
        if (isDarkTheme) ChatBubblePurple else ChatBubblePurpleLight
    } else {
        if (isDarkTheme) ChatBubblePink else ChatBubblePinkLight
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Formats message time in HH:MM format.
 */
fun formatMessageTime(timestamp: String): String {
    return try {
        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        )

        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")

        val date = inputFormat.parse(timestamp)

        val outputFormat = SimpleDateFormat(
            "h:mm a",
            Locale.getDefault()
        )

        date?.let { outputFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}

/**
 * Header for conversation screen showing user info and action buttons.
 */
@Composable
fun ConversationHeader(
    user: ChatUser,
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkText else LightText
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Gray300)
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = user.getDisplayAvatarUrl(),
                    contentDescription = user.fullName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(BrandPink)
                            .align(Alignment.BottomEnd)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }
            }

            Text(
                text = URLDecoder.decode(user.fullName, "UTF-8"),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Input field for typing and sending messages.
 */
@Composable
fun MessageInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (isDarkTheme) DarkText else LightText
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText
    val sendButtonColor = if (isDarkTheme) ChatBubblePurple else ChatBubblePurpleLight
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = { },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Camera",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }

        IconButton(
            onClick = { },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AttachFile,
                contentDescription = "Add",
                tint = textColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(surfaceColor)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = textColor
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = "Nhắn tin...",
                            style = MaterialTheme.typography.bodySmall,
                            color = secondaryTextColor
                        )
                    }
                    innerTextField()
                }
            )
        }

        IconButton(
            onClick = onSendClick,
            enabled = value.isNotBlank(),
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Send,
                contentDescription = "Send",
                tint = if (value.isNotBlank()) {
                    sendButtonColor
                } else {
                    secondaryTextColor
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Conversation detail screen.
 */
@Composable
fun ConversationScreen(
    matchId: Int,
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConversationViewModel = viewModel()
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
                Text("Loading conversation...", color = textColor)
            }
        }

        is ConversationUiState.Success -> {
            ConversationScreenContent(
                conversation = state.conversation,
                onBackClick = onBackClick,
                onAvatarClick = onAvatarClick,
                onSendMessage = { content ->
                    viewModel.sendMessage(content)
                },
                onLoadMoreMessages = {
                    viewModel.loadMoreMessages()
                },
                isLoadingMore = viewModel.isLoadingMoreMessages(),
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
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Error loading conversation",
                        color = textColor
                    )

                    Text(
                        state.message,
                        color = if (isDarkTheme) DarkSecondaryText else LightSecondaryText
                    )
                }
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

/**
 * Internal content composable.
 */
@Composable
private fun ConversationScreenContent(
    conversation: Conversation,
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onLoadMoreMessages: () -> Unit = {},
    currentUserId: Int = 1,
    isLoadingMore: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText

    var messageInput by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    // Scroll to bottom when new messages arrive or initially loaded
    LaunchedEffect(conversation.messages.size) {
        if (conversation.messages.isNotEmpty()) {
            listState.animateScrollToItem(
                index = conversation.messages.size - 1,
                scrollOffset = 0
            )
        }
    }

    // Detect when user scrolls to top to load more messages
    LaunchedEffect(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        if (conversation.messages.isNotEmpty() && !isLoadingMore) {
            val firstVisibleItem = listState.firstVisibleItemIndex
            val firstVisibleItemScrollOffset = listState.firstVisibleItemScrollOffset

            // Trigger load more when user scrolls to first item and scrolls up
            if (firstVisibleItem == 0 && firstVisibleItemScrollOffset < 100) {
                onLoadMoreMessages()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                )
            }
    ) {

        ConversationHeader(
            user = conversation.user,
            onBackClick = onBackClick,
            onAvatarClick = onAvatarClick
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(backgroundColor),
            state = listState,
            reverseLayout = false,
            contentPadding = PaddingValues(
                top = 8.dp,
                bottom = 8.dp
            )
        ) {

            items(
                items = conversation.messages,
                key = { it.messageId }
            ) { message ->

                MessageBubble(
                    message = message,
                    isCurrentUser = message.senderId == currentUserId
                )
            }

            // Show loading indicator at the bottom
            if (isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Loading more messages...",
                            color = secondaryTextColor,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        MessageInputField(
            value = messageInput,
            onValueChange = {
                messageInput = it
            },
            onSendClick = {
                if (messageInput.isNotBlank()) {
                    onSendMessage(messageInput)
                    messageInput = ""
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            }
        )
    }
}