package com.example.dating.data.remote

import com.example.dating.data.model.User
import com.example.dating.ui.chat.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserApiService {

    @GET("api/users/me")
    suspend fun getUser(): ApiResponse<User>

    @PUT("api/users/preferences")
    suspend fun updatePreferences(
        @Body body: Map<String, String>
    ): ApiResponse<Any>
}