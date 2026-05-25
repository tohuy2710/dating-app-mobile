package com.example.dating.data.remote

import com.example.dating.data.model.Avatar
import com.example.dating.data.model.CreatePhotoRequest
import com.example.dating.data.model.Photo
import com.example.dating.data.model.UpdatePreferencesRequest
import com.example.dating.data.model.UpdateProfileRequest
import com.example.dating.data.model.User
import com.example.dating.data.model.UserPreferences
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
        @Body body: UpdatePreferencesRequest
    ): ApiResponse<UserPreferences>

    @POST("api/users/photos")
    suspend fun createPhoto(
        @Body body: CreatePhotoRequest
    ): ApiResponse<Photo>

    @PUT("api/users/profile")
    suspend fun updateProfile(
        @Body body: UpdateProfileRequest
    ): ApiResponse<User>

    @GET("api/users/avatar")
    suspend fun getAvatar(): ApiResponse<Avatar>
}