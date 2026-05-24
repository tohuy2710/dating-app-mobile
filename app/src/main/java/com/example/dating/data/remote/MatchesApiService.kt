package com.example.dating.data.remote

import com.example.dating.ui.chat.AnonymousMatchResponse
import com.example.dating.ui.chat.ApiResponse
import com.example.dating.ui.chat.MatchDetailResponseData
import com.example.dating.ui.chat.MatchesListResponseData
import com.example.dating.ui.chat.Message
import com.example.dating.ui.chat.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MatchesApiService {

    @GET("api/matches")
    suspend fun getMatches(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("search") search: String? = ""
    ): ApiResponse<MatchesListResponseData>

    @GET("api/matches/{matchId}")
    suspend fun getMatchDetail(
        @Path("matchId") matchId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): ApiResponse<MatchDetailResponseData>

    @DELETE("api/matches/{matchId}")
    suspend fun deleteMatch(
        @Path("matchId") matchId: Int
    ): ApiResponse<Unit>

    @POST("api/matches/{matchId}/messages")
    suspend fun sendMessage(
        @Path("matchId") matchId: Int,
        @Body messageRequest: SendMessageRequest
    ): ApiResponse<Message>

    @POST("api/matches/anonymous")
    suspend fun anonymousMatch() : ApiResponse<AnonymousMatchResponse>
}