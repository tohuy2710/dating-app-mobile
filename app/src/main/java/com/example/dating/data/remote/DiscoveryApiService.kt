package com.example.dating.data.remote

import com.example.dating.data.model.DiscoverResponse
import com.example.dating.data.model.InteractionRequest
import com.example.dating.data.model.InteractionResultData
import com.example.dating.data.model.OnboardingStatus
import com.example.dating.ui.chat.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface DiscoveryApiService {

    @GET("api/users/discover")
    suspend fun discoverUsers(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResponse<DiscoverResponse>

    @POST("api/interactions")
    suspend fun createInteraction(
        @Body body: InteractionRequest
    ): ApiResponse<InteractionResultData>

    @GET("api/interactions/requests/received")
    suspend fun getReceivedRequests(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResponse<DiscoverResponse>

    @GET("api/interactions/requests/sent")
    suspend fun getSentRequests(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): ApiResponse<DiscoverResponse>

    @GET("api/users/onboarding/status")
    suspend fun getOnboardingStatus(): ApiResponse<OnboardingStatus>

    @PUT("api/users/onboarding/complete")
    suspend fun completeOnboarding(): ApiResponse<OnboardingStatus>
}