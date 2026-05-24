package com.example.dating.data.repository

import com.example.dating.data.model.CreatePhotoRequest
import com.example.dating.data.model.UpdatePreferencesRequest
import com.example.dating.data.model.User
import com.example.dating.data.remote.UserApiService

class UserRepository(
    private val userApiService: UserApiService
) {

    suspend fun getCurrentUser(): User {
        return userApiService.getUser().data
    }

    suspend fun updateAnonymousInterests(interests: List<String>) {
        userApiService.updatePreferences(
            UpdatePreferencesRequest(
                anonymous_interests = interests
            )
        )
    }

    suspend fun createPhoto(
        imageUrl: String
    ) {

        userApiService.createPhoto(
            CreatePhotoRequest(
                image_url = imageUrl
            )
        )
    }
}