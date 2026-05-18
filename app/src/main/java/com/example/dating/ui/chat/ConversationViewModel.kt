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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.remote.MatchesApiService
import com.example.dating.data.repository.ChatRepository
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class ConversationViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchesApiService: MatchesApiService by lazy {
        RetrofitClient.retrofitInstance.create(MatchesApiService::class.java)
    }
    
    private val chatRepository: ChatRepository by lazy {
        ChatRepository(matchesApiService)
    }
    
    // Extract matchId and conversation from navigation arguments
    val matchId: Int? = savedStateHandle["matchId"]
    private val conversationJson: String? = savedStateHandle["conversation"]
    private val initialConversation: Conversation? = conversationJson?.let {
        try {
            Json.decodeFromString<Conversation>(it)
        } catch (e: Exception) {
            null
        }
    }

    var conversationUiState: ConversationUiState by mutableStateOf(ConversationUiState.Idle)
        private set

    init {
        if (matchId != null && initialConversation != null) {
            loadConversationDetail()
        }
    }

    /**
     * Load conversation details (match + fresh messages) from API.
     * 
     * Uses pre-loaded conversation from getMatches response and fetches fresh messages from getMatchDetail.
     * API Call: GET /api/matches/{matchId}?page=1&limit=50
     */
    private fun loadConversationDetail() {
        conversationUiState = ConversationUiState.Loading

        viewModelScope.launch {
            try {
                val conversation = initialConversation ?: return@launch
                val updatedConversation = chatRepository.fetchConversationDetail(
                    conversation = conversation,
                    messagesPage = 1,
                    messagesLimit = 50
                )
                conversationUiState = ConversationUiState.Success(updatedConversation)
            } catch (e: Exception) {
                conversationUiState = ConversationUiState.Error(
                    e.message ?: "Failed to load conversation"
                )
            }
        }
    }

    /**
     * Send a message in the conversation.
     * 
     * API Call: POST /api/matches/{matchId}/messages
     */
    fun sendMessage(content: String) {
        viewModelScope.launch {
            try {
                val message = chatRepository.sendMessage(matchId ?: return@launch, content)
                
                // Update UI state with new message
                if (conversationUiState is ConversationUiState.Success) {
                    val currentState = conversationUiState as ConversationUiState.Success
                    val updatedConversation = currentState.conversation.copy(
                        messages = currentState.conversation.messages + message,
                        lastMessage = content,
                        lastMessageTime = message.sentAt
                    )
                    conversationUiState = ConversationUiState.Success(updatedConversation)
                }
            } catch (e: Exception) {
                conversationUiState = ConversationUiState.Error(
                    "Failed to send message: ${e.message}"
                )
            }
        }
    }

    /**
     * Refresh conversation from API.
     */
    fun refreshConversation() {
        loadConversationDetail()
    }
    
    /**
     * Delete/Unmatch the conversation.
     */
    fun deleteMatch(matchId: Int) {
        viewModelScope.launch {
            try {
                chatRepository.deleteMatch(matchId)
            } catch (e: Exception) {
                conversationUiState = ConversationUiState.Error(
                    "Failed to delete match: ${e.message}"
                )
            }
        }
    }
}

/**
 * UI State for conversation screen.
 */
sealed class ConversationUiState {
    data object Idle : ConversationUiState()
    data object Loading : ConversationUiState()
    data class Success(val conversation: Conversation) : ConversationUiState()
    data class Error(val message: String) : ConversationUiState()
}
