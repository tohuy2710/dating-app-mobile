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

class ConversationViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val matchesApiService: MatchesApiService by lazy {
        RetrofitClient.retrofitInstance.create(MatchesApiService::class.java)
    }

    private val chatRepository: ChatRepository by lazy {
        ChatRepository(matchesApiService)
    }

    /**
     * Get matchId directly from navigation arguments.
     *
     * Route:
     * conversation/{matchId}
     */
    private val matchId: Int? = savedStateHandle["matchId"]

    var conversationUiState: ConversationUiState by mutableStateOf(
        ConversationUiState.Idle
    )
        private set

    init {
        if (matchId != null) {
            loadConversationDetail()
        } else {
            conversationUiState =
                ConversationUiState.Error(
                    "Invalid match id"
                )
        }
    }

    /**
     * Fetch conversation detail directly from API.
     *
     * API:
     * GET /api/matches/{matchId}
     */
    private fun loadConversationDetail() {

        conversationUiState =
            ConversationUiState.Loading

        viewModelScope.launch {
            try {

                val id = matchId ?: return@launch

                val conversation =
                    chatRepository.fetchConversationFromMatchDetail(
                        matchId = id,
                        messagesPage = 1,
                        messagesLimit = 50
                    )

                conversationUiState =
                    ConversationUiState.Success(conversation)

            } catch (e: Exception) {

                conversationUiState =
                    ConversationUiState.Error(
                        e.message
                            ?: "Failed to load conversation"
                    )
            }
        }
    }

    /**
     * Send message.
     *
     * API:
     * POST /api/matches/{matchId}/messages
     */
    fun sendMessage(content: String) {

        if (content.isBlank()) return

        viewModelScope.launch {

            try {

                val id = matchId ?: return@launch

                val message =
                    chatRepository.sendMessage(
                        matchId = id,
                        content = content
                    )

                val currentState = conversationUiState

                if (currentState is ConversationUiState.Success) {

                    val updatedConversation =
                        currentState.conversation.copy(

                            messages =
                                currentState.conversation.messages + message,

                            lastMessage = message.content,

                            lastMessageTime = message.sentAt
                        )

                    conversationUiState =
                        ConversationUiState.Success(
                            updatedConversation
                        )
                }

            } catch (e: Exception) {

                conversationUiState =
                    ConversationUiState.Error(
                        "Failed to send message: ${e.message}"
                    )
            }
        }
    }

    /**
     * Refresh conversation.
     */
    fun refreshConversation() {
        loadConversationDetail()
    }

    /**
     * Delete / Unmatch conversation.
     */
    fun deleteMatch() {

        viewModelScope.launch {

            try {

                val id = matchId ?: return@launch

                chatRepository.deleteMatch(id)

            } catch (e: Exception) {

                conversationUiState =
                    ConversationUiState.Error(
                        "Failed to delete match: ${e.message}"
                    )
            }
        }
    }
}

/**
 * UI state for ConversationScreen.
 */
sealed class ConversationUiState {

    data object Idle : ConversationUiState()

    data object Loading : ConversationUiState()

    data class Success(
        val conversation: Conversation
    ) : ConversationUiState()

    data class Error(
        val message: String
    ) : ConversationUiState()
}