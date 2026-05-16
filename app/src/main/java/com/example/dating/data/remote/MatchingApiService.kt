package com.example.dating.data.remote

import com.example.dating.data.model.*
import retrofit2.http.*

interface MatchingApiService {
    
    // --- Interactions ---

    @POST("interactions/request/{userId}")
    suspend fun sendInteraction(
        @Path("userId") userId: Int,
        @Body request: InteractionRequest
    ): ApiResponse<InteractionData>

    @GET("interactions/requests/pending-received")
    suspend fun getPendingReceived(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ApiResponse<InteractionListResponse>

    @GET("interactions/requests/pending-sent")
    suspend fun getPendingSent(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ApiResponse<InteractionListResponse>

    @POST("interactions/{interactionId}/accept")
    suspend fun acceptInteraction(
        @Path("interactionId") interactionId: Int,
        @Body body: Map<String, String> = mapOf("interaction_mode" to "traditional")
    ): ApiResponse<MatchData>

    @POST("interactions/{interactionId}/reject")
    suspend fun rejectInteraction(
        @Path("interactionId") interactionId: Int,
        @Body body: Map<String, String> = mapOf("interaction_mode" to "traditional")
    ): ApiResponse<InteractionData>

    @GET("interactions/status/{userId}")
    suspend fun getInteractionStatus(
        @Path("userId") userId: Int
    ): ApiResponse<InteractionStatusResponse>

    @GET("users/discover")
    suspend fun getDiscoveryUsers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ApiResponse<DiscoveryResponse>

    // --- Matches ---

    @GET("matches")
    suspend fun getMatches(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): ApiResponse<MatchListResponse>

    @GET("matches/{matchId}")
    suspend fun getMatchDetail(
        @Path("matchId") matchId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): ApiResponse<MatchDetailResponse>

    @POST("matches/{matchId}/messages")
    suspend fun sendMessage(
        @Path("matchId") matchId: Int,
        @Body request: SendMessageRequest
    ): ApiResponse<MessageData>

    @DELETE("matches/{matchId}")
    suspend fun unmatch(
        @Path("matchId") matchId: Int
    ): ApiResponse<Unit>
}
