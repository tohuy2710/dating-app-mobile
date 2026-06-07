package com.example.dating.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DiscoverResponse(
    val users: List<User>,
    val pagination: PaginationInfo
)

@Serializable
data class PaginationInfo(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

@Serializable
data class InteractionRequest(
    val target_id: Int,
    val action_type: String,
    val interaction_mode: String = "traditional"
)

@Serializable
data class InteractionResultData(
    val interaction: Interaction,
    val match: Match? = null
)

@Serializable
data class Interaction(
    val interaction_id: Int,
    val actor_id: Int,
    val target_id: Int,
    val action_type: String,
    val interaction_mode: String,
    val created_at: String
)

@Serializable
data class Match(
    val match_id: Int,
    val user1_id: Int,
    val user2_id: Int,
    val match_mode: String,
    val is_active: Boolean,
    val matched_at: String,
    val user1: UserBase? = null,
    val user2: UserBase? = null
)

@Serializable
data class UserBase(
    val user_id: Int,
    val email: String? = null,
    val full_name: String,
    val birth_date: String? = null,
    val gender: String? = null,
    val bio: String? = null,
    val default_mode: String? = null,
    val created_at: String? = null
)