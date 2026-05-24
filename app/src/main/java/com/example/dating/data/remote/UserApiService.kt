package com.example.dating.data.remote

import com.example.dating.data.model.CreatePhotoRequest
import com.example.dating.data.model.Photo
import com.example.dating.data.model.User
import com.example.dating.ui.chat.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserApiService {

    @GET("api/users/me")
    suspend fun getUser(): ApiResponse<User>

    @PUT("api/users/preferences")
    suspend fun updatePreferences(
        @Body body: Map<String, String>
    ): ApiResponse<Any>

    @POST("api/users/photos")
    suspend fun createPhoto(
        @Body body: CreatePhotoRequest
    ): ApiResponse<Photo>

}