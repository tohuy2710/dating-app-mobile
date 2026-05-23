package com.example.dating.data.remote

import com.example.dating.data.model.DiscoverResponse
import com.example.dating.data.model.InteractionRequest
import com.example.dating.data.model.InteractionResultData
import com.example.dating.ui.chat.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}