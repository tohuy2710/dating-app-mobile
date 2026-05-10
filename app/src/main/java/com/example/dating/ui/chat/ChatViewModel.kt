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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * ViewModel for managing chat/messaging related state and business logic.
 * Handles conversations, messages, and suggested connections.
 * 
 * Mock data is based on the dating app database schema:
 * - Main user: id=1 (mainuser@gmail.com)
 * - Matches: user 1 with user 2 (Alice) and user 3 (Emma)
 * - Messages for each match conversation
 */
class ChatViewModel : ViewModel() {

    var chatUiState: ChatUiState by mutableStateOf(ChatUiState.Idle)
        private set

    var searchQuery: String by mutableStateOf("")
        private set

    companion object {
        private const val MAIN_USER_ID = 1
    }

    init {
        loadChatData()
    }

    /**
     * Loads mock chat data based on database schema.
     * Simulates:
     * - 2 active matches for the main user
     * - Messages within each conversation
     * - Suggested connections from non-matched users
     */
    private fun loadChatData() {
        chatUiState = ChatUiState.Loading

        try {
            val conversations = MockChatData.generateConversations()
            val suggestions = MockChatData.generateSuggestions()

            chatUiState = ChatUiState.Success(
                conversations = conversations,
                suggestions = suggestions
            )
        } catch (e: Exception) {
            chatUiState = ChatUiState.Error(e.message ?: "Unknown error occurred")
        }
    }

    /**
     * Updates the search query and filters conversations.
     */
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    /**
     * Handles connection action for suggested users.
     */
    fun connectWithUser(user: ChatUser) {
        // TODO: Implement connect action with API call
        // - Create interaction record (LIKE)
        // - Check for mutual like to create match
    }

    /**
     * Opens or switches to a specific conversation.
     */
    fun selectConversation(conversation: Conversation) {
        // TODO: Implement conversation selection and navigation
    }

    /**
     * Sends a message in the current conversation.
     */
    fun sendMessage(matchId: Int, content: String) {
        // TODO: Implement message sending with API call
        // - Insert into messages table
        // - Update match timestamps
        // - Handle real-time updates
    }
}
