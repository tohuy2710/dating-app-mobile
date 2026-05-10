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

import kotlinx.serialization.Serializable

/**
 * Data model for a user profile in chat.
 * Maps to users table from database.
 */
@Serializable
data class ChatUser(
    val userId: Int,
    val fullName: String,
    val email: String,
    val bio: String? = null,
    val birthDate: String? = null,
    val gender: String? = null,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Data model for a single message in a conversation.
 * Maps to messages table from database.
 */
@Serializable
data class Message(
    val messageId: Int,
    val matchId: Int,
    val senderId: Int,
    val content: String,
    val sentAt: Long,
    val isRead: Boolean = false
)

/**
 * Data model for a match between two users.
 * Maps to matches table from database.
 */
@Serializable
data class Match(
    val matchId: Int,
    val user1Id: Int,
    val user2Id: Int,
    val matchMode: String = "traditional",
    val matchedAt: Long,
    val isActive: Boolean = true
)

/**
 * Data model for a conversation/chat thread.
 * Combines match and related data.
 */
@Serializable
data class Conversation(
    val matchId: Int,
    val user: ChatUser,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int = 0,
    val isTyping: Boolean = false,
    val messages: List<Message> = emptyList(),
    val matchMode: String = "traditional"
)

/**
 * Data model for a suggested connection.
 */
@Serializable
data class SuggestedConnection(
    val userId: Int,
    val user: ChatUser,
    val mutualFriendsCount: Int = 0
)

/**
 * UI state for chat screen.
 */
sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Success(
        val conversations: List<Conversation>,
        val suggestions: List<SuggestedConnection>
    ) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
    data object Idle : ChatUiState()
}
