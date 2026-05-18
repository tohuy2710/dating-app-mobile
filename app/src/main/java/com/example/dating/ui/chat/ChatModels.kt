package com.example.dating.ui.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    @SerialName("photo_id")
    val photoId: Int,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("is_primary")
    val isPrimary: Boolean = false
)

@Serializable
data class ChatUser(

    @SerialName("user_id")
    val userId: Int,

    @SerialName("full_name")
    val fullName: String,

    val email: String = "",

    val bio: String? = null,

    @SerialName("birth_date")
    val birthDate: String? = null,

    val gender: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("is_online")
    val isOnline: Boolean = false,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    val photos: List<Photo> = emptyList()
) {
    fun getDisplayAvatarUrl(): String {
        val photo = photos.find { it.isPrimary } ?: photos.firstOrNull()
        return photo?.imageUrl ?: avatarUrl ?: ""
    }
}

@Serializable
data class Message(

    @SerialName("message_id")
    val messageId: Int,

    @SerialName("match_id")
    val matchId: Int,

    @SerialName("sender_id")
    val senderId: Int,

    val content: String,

    @SerialName("sent_at")
    val sentAt: String,

    @SerialName("is_read")
    val isRead: Boolean = false
)

@Serializable
data class Match(

    @SerialName("match_id")
    val matchId: Int,

    @SerialName("user1_id")
    val user1Id: Int,

    @SerialName("user2_id")
    val user2Id: Int,

    @SerialName("match_mode")
    val matchMode: String = "traditional",

    @SerialName("matched_at")
    val matchedAt: String,

    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class SendMessageRequest(
    val content: String
)

@Serializable
data class Conversation(
    val matchId: Int,
    val user: ChatUser,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int = 0,
    val isTyping: Boolean = false,
    val messages: List<Message> = emptyList(),
    val matchMode: String = "traditional"
)

@Serializable
data class SuggestedConnection(
    val userId: Int,
    val user: ChatUser,
    val mutualFriendsCount: Int = 0
)

@Serializable
data class PaginationInfo(

    val page: Int = 1,

    val limit: Int = 10,

    val total: Int = 0,

    @SerialName("totalPages")
    val totalPages: Int = 1
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val statusCode: Int,
    val message: String,
    val data: T
)

@Serializable
data class MatchesListResponseData(
    val matches: List<MatchWithUsers> = emptyList(),
    val pagination: PaginationInfo = PaginationInfo()
)

@Serializable
data class MatchWithUsers(

    @SerialName("match_id")
    val matchId: Int,

    @SerialName("user1_id")
    val user1Id: Int,

    @SerialName("user2_id")
    val user2Id: Int,

    @SerialName("match_mode")
    val matchMode: String = "traditional",

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("matched_at")
    val matchedAt: String,

    val user1: ChatUser,

    val user2: ChatUser,

    @SerialName("unreadCount")
    val unreadCount: Int = 0,

    @SerialName("unreadMessages")
    val unreadMessages: List<Message> = emptyList(),

    @SerialName("lastMessage")
    val lastMessage: Message
)

@Serializable
data class MatchDetailResponseData(
    val match: Match,
    val messages: List<Message> = emptyList(),

    @SerialName("messagesPagination")
    val messagesPagination: PaginationInfo = PaginationInfo()
)

sealed class ChatUiState {
    data object Loading : ChatUiState()

    data class Success(
        val conversations: List<Conversation>,
        val suggestions: List<SuggestedConnection>
    ) : ChatUiState()

    data class Error(val message: String) : ChatUiState()

    data object Idle : ChatUiState()
}