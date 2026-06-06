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

import android.util.Log
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
import kotlinx.serialization.decodeFromString
import org.json.JSONObject
import com.example.dating.core.socket.SocketManager
import com.example.dating.ui.auth.LoginScreen

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

    // Pagination tracking
    private var currentPage: Int = 1
    private val messagesPerPage: Int = 50
    private var isLoadingMore: Boolean = false
    private var hasMoreMessages: Boolean = true

    init {
        if (matchId != null) {
            loadConversationDetail()
            observeIncomingMessages()
        } else {
            conversationUiState =
                ConversationUiState.Error(
                    "Invalid match id"
                )
        }
    }

    private fun observeIncomingMessages() {
        viewModelScope.launch {
            SocketManager.newMessageEvent.collect { data ->
                handleIncomingMessage(data)
            }
        }
    }

    private fun handleIncomingMessage(data: JSONObject) {
        try {
            val incomingMatchId = data.optInt("matchId", -1)
            val currentMatchId = matchId ?: return

            if (incomingMatchId == currentMatchId) {
                val messageObj = data.getJSONObject("message")
                val json = Json { ignoreUnknownKeys = true }
                val incomingMessage = json.decodeFromString<Message>(messageObj.toString())
                val currentState = conversationUiState
                if (currentState is ConversationUiState.Success) {
                    val isDuplicate = currentState.conversation.messages.any {
                        it.messageId == incomingMessage.messageId
                    }
                    if (!isDuplicate) {
                        val updatedConversation = currentState.conversation.copy(
                            messages = currentState.conversation.messages + incomingMessage,
                            lastMessage = incomingMessage.content,
                            lastMessageTime = incomingMessage.sentAt
                        )
                        conversationUiState = ConversationUiState.Success(updatedConversation)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

    /**
     * Load more messages (older messages when scrolling up).
     *
     * API:
     * GET /api/matches/{matchId}?page={page}
     */
    fun loadMoreMessages() {

        if (isLoadingMore || !hasMoreMessages) return

        isLoadingMore = true

        viewModelScope.launch {

            try {

                val id = matchId ?: return@launch

                currentPage++

                val response =
                    chatRepository.fetchConversationMessagesPage(
                        matchId = id,
                        messagesPage = currentPage,
                        messagesLimit = messagesPerPage
                    )

                val currentState = conversationUiState

                if (currentState is ConversationUiState.Success) {

                    val newMessages = response.messages

                    if (newMessages.isEmpty()) {
                        hasMoreMessages = false
                    } else {

                        // Prepend old messages to maintain chronological order
                        val updatedConversation =
                            currentState.conversation.copy(
                                messages = newMessages + currentState.conversation.messages
                            )

                        conversationUiState =
                            ConversationUiState.Success(
                                updatedConversation
                            )
                    }
                }

            } catch (e: Exception) {

                currentPage-- // Revert if failed

                conversationUiState =
                    ConversationUiState.Error(
                        "Failed to load more messages: ${e.message}"
                    )

            } finally {
                isLoadingMore = false
            }
        }
    }

    /**
     * Check if currently loading more messages.
     */
    fun isLoadingMoreMessages(): Boolean = isLoadingMore

    fun requestUpgrade() {
        viewModelScope.launch {
            try {
                val id = matchId ?: return@launch
                Log.d("ChatOptions", "Đang gửi requestUpgrade cho matchId: $id")

                val request = chatRepository.requestUpgrade(id)
                Log.d("ChatOptions", "Gửi requestUpgrade thành công: $request")

                val currentState = conversationUiState
                if (currentState is ConversationUiState.Success) {
                    val updatedConversation = currentState.conversation.copy(
                        pendingUpgradeRequest = request
                    )
                    conversationUiState = ConversationUiState.Success(updatedConversation)
                }
            } catch (e: Exception) {
                Log.e("ChatOptions", "Lỗi requestUpgrade: ${e.message}", e) // HIỂN THỊ LỖI LÊN LOGCAT
            }
        }
    }

    fun respondToUpgrade(status: String) {
        viewModelScope.launch {
            try {
                val id = matchId ?: return@launch
                Log.d("ChatOptions", "Đang gửi respondToUpgrade với status: $status cho matchId: $id")

                chatRepository.respondToUpgrade(id, status)
                Log.d("ChatOptions", "Respond thành công!")

                loadConversationDetail()
            } catch (e: Exception) {
                Log.e("ChatOptions", "Lỗi respondToUpgrade: ${e.message}", e) // HIỂN THỊ LỖI LÊN LOGCAT
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