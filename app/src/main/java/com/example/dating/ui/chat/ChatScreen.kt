package com.example.dating.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.dating.ui.theme.DarkBackground
import com.example.dating.ui.theme.DarkText
import com.example.dating.ui.theme.DarkSecondaryText
import com.example.dating.ui.theme.LightBackground
import com.example.dating.ui.theme.LightText
import com.example.dating.ui.theme.LightSecondaryText
import com.example.dating.ui.theme.BorderGray

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onConversationSelected: (Conversation) -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackground else LightBackground
    val textColor = if (isDarkTheme) DarkText else LightText
    val secondaryTextColor = if (isDarkTheme) DarkSecondaryText else LightSecondaryText
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            }
    ) {
        when (val state = viewModel.chatUiState) {

            is ChatUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ChatUiState.Success -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundColor),
                    verticalArrangement = Arrangement.Top
                ) {

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(backgroundColor)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            Text(
                                text = "Hòm thư",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                            ChatSearchBar(
                                searchQuery = viewModel.searchQuery,
                                onSearchQueryChange = viewModel::updateSearchQuery
                            )
                        }
                    }

                    if (state.suggestions.isNotEmpty()) {

                        item {
                            SuggestedConnectionsList(
                                suggestions = state.suggestions,
                                onConnect = viewModel::connectWithUser,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Tin nhắn",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = secondaryTextColor,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        )
                    }

                    if (state.conversations.isNotEmpty()) {

                        items(
                            items = state.conversations,
                            key = { it.matchId }
                        ) { conversation ->

                            MessageItem(
                                conversation = conversation,
                                onSelectConversation = { selected ->
                                    onConversationSelected(selected)
                                    viewModel.selectConversation(selected)
                                }
                            )
                        }

                    } else {

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Không có tin nhắn nào",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = secondaryTextColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            is ChatUiState.Error -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is ChatUiState.Idle -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}