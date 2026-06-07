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
import androidx.lifecycle.viewModelScope
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.remote.MatchesApiService
import com.example.dating.data.repository.ChatRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

/**
 * ViewModel for managing chat/messaging related state and business logic.
 * Handles conversations, messages, and suggested connections.
 * 
 * Loads data from real API endpoints via ChatRepository:
 * - GET /api/matches - Fetch user matches
 * - GET /api/matches/{matchId} - Fetch match details with messages
 * - POST /api/matches/{matchId}/messages - Send messages
 */
class ChatViewModel : ViewModel() {

    private val matchesApiService: MatchesApiService by lazy {
        RetrofitClient.retrofitInstance.create(MatchesApiService::class.java)
    }
    
    private val chatRepository: ChatRepository by lazy {
        ChatRepository(matchesApiService)
    }

    private var searchJob: Job? = null

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
     * Loads chat data from API.
     * 
     * Fetches:
     * - User matches via GET /api/matches
     * - Suggested connections (when endpoint available)
     */
    private fun loadChatData(
        showLoading: Boolean = true
    ) {
        if (showLoading) {
            chatUiState = ChatUiState.Loading
        }

        viewModelScope.launch {
            try {
                val conversations = chatRepository.fetchConversations(page = 1, limit = 10)
                val suggestions = chatRepository.fetchSuggestedConnections()

                chatUiState = ChatUiState.Success(
                    conversations = conversations,
                    suggestions = suggestions
                )
            } catch (e: Exception) {
                chatUiState = ChatUiState.Error(
                    e.message ?: "Unknown error occurred while fetching conversations"
                )
            }
        }
    }

    /**
     * Updates the search query and filters conversations.
     */
    fun updateSearchQuery(query: String) {
        searchQuery = query

        searchJob?.cancel()

        searchJob = viewModelScope.launch {

            delay(1000)

            performSearch(query)
        }
    }

    private suspend fun searchConversations(search: String) {

        try {

            val conversations =
                chatRepository.fetchConversations(
                    page = 1,
                    limit = 10,
                    search = search.ifBlank { null }
                )

            val currentState = chatUiState

            if (currentState is ChatUiState.Success) {

                chatUiState = currentState.copy(
                    conversations = conversations
                )
            } else {

                chatUiState = ChatUiState.Success(
                    conversations = conversations,
                    suggestions = emptyList()
                )
            }

        } catch (e: Exception) {

            chatUiState =
                ChatUiState.Error(
                    e.message ?: "Failed to search conversations"
                )
        }
    }

    private fun performSearch(query: String) {

        viewModelScope.launch {

            searchConversations(query)
        }
    }

    /**
     * Handles connection action for suggested users.
     * 
     * TODO: Implement API call to:
     * - Create interaction record (LIKE)
     * - Check for mutual like to create match
     */
    fun connectWithUser(user: ChatUser) {
        viewModelScope.launch {
            try {
                // TODO: Implement interaction API call
                // POST /api/interactions with user_id
            } catch (e: Exception) {
                chatUiState = ChatUiState.Error("Failed to connect with user: ${e.message}")
            }
        }
    }

    /**
     * Opens or switches to a specific conversation.
     */
    fun selectConversation(conversation: Conversation) {
        // Navigation handled by UI layer
    }

    /**
     * Sends a message in the current conversation.
     * 
     * API Call: POST /api/matches/{matchId}/messages
     */
    fun sendMessage(matchId: Int, content: String) {
        viewModelScope.launch {
            try {
                val sentMessage = chatRepository.sendMessage(matchId, content)
                
                // Update UI state with new message
                if (chatUiState is ChatUiState.Success) {
                    val currentState = chatUiState as ChatUiState.Success
                    val updatedConversations = currentState.conversations.map { conversation ->
                        if (conversation.matchId == matchId) {
                            conversation.copy(
                                messages = conversation.messages + sentMessage,
                                lastMessage = content,
                                lastMessageTime = sentMessage.sentAt.toString()
                            )
                        } else {
                            conversation
                        }
                    }
                    chatUiState = ChatUiState.Success(
                        conversations = updatedConversations,
                        suggestions = currentState.suggestions
                    )
                }
            } catch (e: Exception) {
                chatUiState = ChatUiState.Error("Failed to send message: ${e.message}")
            }
        }
    }
    
    /**
     * Unmatch a specific match.
     * 
     * API Call: DELETE /api/matches/{matchId}
     */
    fun unMatch(matchId: Int) {
        viewModelScope.launch {
            try {
                chatRepository.deleteMatch(matchId)
                
                // Remove match from UI state
                if (chatUiState is ChatUiState.Success) {
                    val currentState = chatUiState as ChatUiState.Success
                    val updatedConversations = currentState.conversations.filter { 
                        it.matchId != matchId 
                    }
                    chatUiState = ChatUiState.Success(
                        conversations = updatedConversations,
                        suggestions = currentState.suggestions
                    )
                }
            } catch (e: Exception) {
                chatUiState = ChatUiState.Error("Failed to unmatch: ${e.message}")
            }
        }
    }
    
    /**
     * Refresh conversations from API.
     */
    fun refreshConversations() {
        loadChatData(showLoading = false)
    }

    fun clearSearch() {
        if (searchQuery.isNotEmpty()) {
            searchQuery = ""
            searchJob?.cancel()
        }
    }
}
