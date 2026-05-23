package com.example.dating.data.repository

import com.example.dating.data.model.User
import com.example.dating.data.remote.UserApiService

class UserRepository(
    private val userApiService: UserApiService
) {

    suspend fun getCurrentUser(): User {
        return userApiService.getUser().data
    }

    suspend fun updateAnonymousInterests(
        interests: String
    ) {

        userApiService.updatePreferences(
            mapOf(
                "anonymous_interests" to interests
            )
        )
    }
}