package com.example.dating.data.repository

import com.example.dating.core.auth.UserManager
import com.example.dating.data.remote.MatchesApiService
import com.example.dating.ui.chat.SendMessageRequest
import com.example.dating.ui.chat.Conversation
import com.example.dating.ui.chat.MatchWithUsers
import com.example.dating.ui.chat.Message
import com.example.dating.ui.chat.SuggestedConnection
import com.example.dating.ui.chat.ChatUser
import com.example.dating.ui.chat.MatchDetailResponseData
import com.example.dating.ui.chat.MatchUpgradeRequest
import com.example.dating.ui.chat.RespondUpgradeRequest

class ChatRepository(
    private val matchesApiService: MatchesApiService
) {
    val currentUserId = UserManager.getUserId()

    suspend fun fetchConversations(
        page: Int,
        limit: Int,
        search: String? = null
    ): List<Conversation> {

        return try {

            val response = matchesApiService.getMatches(page, limit, search)

            response.data.matches.map { matchWithUsers: MatchWithUsers ->

                val otherUser =
                    if (matchWithUsers.user1Id == currentUserId) {
                        matchWithUsers.user2
                    } else {
                        matchWithUsers.user1
                    }

                val lastMessage = matchWithUsers.lastMessage?.content ?: "Gửi lời chào đầu tiên đi 👋"

                Conversation(
                    matchId = matchWithUsers.matchId,
                    user = otherUser,
                    lastMessage = lastMessage,
                    lastMessageTime = matchWithUsers.matchedAt,
                    unreadCount = matchWithUsers.unreadCount,
                    isTyping = false,
                    messages = emptyList(),
                    matchMode = matchWithUsers.matchMode
                )
            }

        } catch (e: Exception) {

            throw Exception(
                "Failed to fetch conversations: ${e.message}",
                e
            )
        }
    }

    suspend fun fetchConversationDetail(
        conversation: Conversation,
        messagesPage: Int = 1,
        messagesLimit: Int = 50
    ): Conversation {

        return try {

            val response =
                matchesApiService.getMatchDetail(
                    conversation.matchId,
                    messagesPage,
                    messagesLimit
                )

            // Use pre-loaded match detail from getMatches, only update messages from getMatchDetail
            val sortedMessages = response.data.messages.sortedBy { it.sentAt }
            conversation.copy(
                messages = sortedMessages,
                lastMessage = sortedMessages.lastOrNull()?.content ?: conversation.lastMessage,
                lastMessageTime = sortedMessages.lastOrNull()?.sentAt ?: conversation.lastMessageTime
            )

        } catch (e: Exception) {

            throw Exception(
                "Failed to fetch conversation detail for match ${conversation.matchId}: ${e.message}",
                e
            )
        }
    }

    suspend fun fetchConversationFromMatchDetail(
        matchId: Int,
        messagesPage: Int = 1,
        messagesLimit: Int = 50
    ): Conversation {

        return try {

            val response =
                matchesApiService.getMatchDetail(
                    matchId,
                    messagesPage,
                    messagesLimit
                )

            val data = response.data

            // Determine other user (not the current user)
            val otherUser = if (data.user1 != null && data.user1.userId == currentUserId) {
                data.user2 ?: data.user1
            } else {
                data.user1 ?: data.user2 ?: ChatUser(0, "Unknown")
            }

            val lastMessage = data.lastMessage?.content ?: data.messages.lastOrNull()?.content ?: "Start chatting!"
            val lastMessageTime = data.lastMessage?.sentAt ?: data.messages.lastOrNull()?.sentAt ?: ""

            val sortedMessages = data.messages.sortedBy { it.sentAt }

            Conversation(
                matchId = matchId,
                user = otherUser,
                lastMessage = lastMessage,
                lastMessageTime = lastMessageTime,
                unreadCount = data.unreadCount,
                isTyping = false,
                messages = sortedMessages,
                matchMode = data.matchMode,
                pendingUpgradeRequest = data.pendingUpgradeRequest
            )

        } catch (e: Exception) {

            throw Exception(
                "Failed to fetch conversation detail for match $matchId: ${e.message}",
                e
            )
        }
    }

    suspend fun fetchConversationMessagesPage(
        matchId: Int,
        messagesPage: Int = 1,
        messagesLimit: Int = 50
    ): MatchDetailResponseData {

        return try {

            val response =
                matchesApiService.getMatchDetail(
                    matchId,
                    messagesPage,
                    messagesLimit
                )

            val sortedMessages = response.data.messages.sortedBy { it.sentAt }

            response.data.copy(
                messages = sortedMessages
            )

        } catch (e: Exception) {

            throw Exception(
                "Failed to fetch messages for match $matchId: ${e.message}",
                e
            )
        }
    }

    suspend fun sendMessage(
        matchId: Int,
        content: String
    ): Message {

        return try {

            val request = SendMessageRequest(
                content = content
            )

            val response =
                matchesApiService.sendMessage(
                    matchId,
                    request
                )

            response.data

        } catch (e: Exception) {

            throw Exception(
                "Failed to send message: ${e.message}",
                e
            )
        }
    }

    suspend fun deleteMatch(matchId: Int) {

        try {

            matchesApiService.deleteMatch(matchId)

        } catch (e: Exception) {

            throw Exception(
                "Failed to delete match $matchId: ${e.message}",
                e
            )
        }
    }

    suspend fun fetchSuggestedConnections(): List<SuggestedConnection> {
        return emptyList()
    }

    suspend fun requestUpgrade(matchId: Int): MatchUpgradeRequest {
        return matchesApiService.requestUpgrade(matchId).data
    }

    suspend fun respondToUpgrade(matchId: Int, status: String) {
        matchesApiService.respondToUpgrade(matchId, RespondUpgradeRequest(status))
    }
}