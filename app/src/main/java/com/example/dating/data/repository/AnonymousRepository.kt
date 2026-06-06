package com.example.dating.data.repository

import com.example.dating.data.remote.MatchesApiService
import com.example.dating.ui.chat.AnonymousMatchResponse

class AnonymousRepository(
    private val apiService: MatchesApiService
) {

    suspend fun anonymousMatch(): AnonymousMatchResponse {

        val response =
            apiService.anonymousMatch()

        if (!response.success) {
            throw Exception(response.message)
        }

        return response.data
    }

    suspend fun cancelAnonymousQueue() {
        try {
            apiService.cancelAnonymousMatchQueue()
        } catch (e: Exception) {
            throw Exception("Failed to cancel anonymous queue: ${e.message}", e)
        }
    }
}