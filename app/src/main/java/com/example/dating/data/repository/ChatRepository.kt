package com.example.dating.data.repository

import com.example.dating.data.remote.MatchesApiService
import com.example.dating.ui.chat.SendMessageRequest
import com.example.dating.ui.chat.Conversation
import com.example.dating.ui.chat.MatchWithUsers
import com.example.dating.ui.chat.Message
import com.example.dating.ui.chat.SuggestedConnection

class ChatRepository(
    private val matchesApiService: MatchesApiService
) {

    companion object {
        private const val MAIN_USER_ID = 1
    }

    suspend fun fetchConversations(
        page: Int,
        limit: Int
    ): List<Conversation> {

        return try {

            val response = matchesApiService.getMatches(page, limit)

            response.data.matches.map { matchWithUsers: MatchWithUsers ->

                val otherUser =
                    if (matchWithUsers.user1Id == MAIN_USER_ID) {
                        matchWithUsers.user2
                    } else {
                        matchWithUsers.user1
                    }

                val lastMessage = matchWithUsers.lastMessage.content ?: "Start chatting!"

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

    suspend fun fetchMatchMessages(
        matchId: Int,
        page: Int = 1,
        limit: Int = 50
    ): List<Message> {

        return try {

            val response =
                matchesApiService.getMatchDetail(
                    matchId,
                    page,
                    limit
                )

            response.data.messages

        } catch (e: Exception) {

            throw Exception(
                "Failed to fetch messages for match $matchId: ${e.message}",
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
            conversation.copy(
                messages = response.data.messages,
                lastMessage = response.data.messages.lastOrNull()?.content ?: conversation.lastMessage,
                lastMessageTime = response.data.messages.lastOrNull()?.sentAt ?: conversation.lastMessageTime
            )

        } catch (e: Exception) {

            throw Exception(
                "Failed to fetch conversation detail for match ${conversation.matchId}: ${e.message}",
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
}