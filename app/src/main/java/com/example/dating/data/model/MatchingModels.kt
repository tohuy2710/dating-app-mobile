package com.example.dating.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InteractionRequest(
    @SerialName("action_type")
    val actionType: String, // LIKE, PASS
    @SerialName("interaction_mode")
    val interactionMode: String // traditional, anonymous
)

@Serializable
data class InteractionData(
    @SerialName("interaction_id")
    val interactionId: Int? = null,
    @SerialName("actor_id")
    val actorId: Int? = null,
    @SerialName("target_id")
    val targetId: Int? = null,
    @SerialName("action_type")
    val actionType: String? = null,
    @SerialName("interaction_mode")
    val interactionMode: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val actor: User? = null,
    val target: User? = null
)

@Serializable
data class InteractionListResponse(
    val requests: List<InteractionData>,
    val pagination: PaginationData
)

@Serializable
data class PaginationData(
    val total: Int,
    val page: Int,
    val limit: Int,
    @SerialName("total_pages")
    val totalPages: Int = 1
)

@Serializable
data class MatchData(
    @SerialName("match_id")
    val matchId: Int,
    @SerialName("user1_id")
    val user1Id: Int,
    @SerialName("user2_id")
    val user2Id: Int,
    @SerialName("match_mode")
    val matchMode: String,
    @SerialName("matched_at")
    val matchedAt: String,
    @SerialName("is_active")
    val isActive: Boolean,
    val user: User? = null // Often the other user in lists
)

@Serializable
data class MatchListResponse(
    val matches: List<MatchData>,
    val pagination: PaginationData
)

@Serializable
data class MessageData(
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
    val isRead: Boolean
)

@Serializable
data class MatchDetailResponse(
    val match: MatchData,
    val messages: List<MessageData>,
    @SerialName("messagesPagination")
    val messagesPagination: PaginationData
)

@Serializable
data class SendMessageRequest(
    val content: String
)

@Serializable
data class InteractionStatusResponse(
    val status: String, // none, outgoing_like, incoming_like, matched, passed
    val match: MatchData? = null,
    @SerialName("outgoingInteraction")
    val outgoingInteraction: InteractionData? = null,
    @SerialName("incomingInteraction")
    val incomingInteraction: InteractionData? = null
)

@Serializable
data class DiscoveryResponse(
    val users: List<User>,
    val pagination: PaginationData
)
