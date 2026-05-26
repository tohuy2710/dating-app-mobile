package com.example.dating.data.repository

import com.example.dating.data.model.DiscoverResponse
import com.example.dating.data.model.InteractionRequest
import com.example.dating.data.model.InteractionResultData
import com.example.dating.data.remote.DiscoveryApiService

class DiscoveryRepository(
    private val api: DiscoveryApiService
) {

    suspend fun discoverUsers(
        page: Int,
        limit: Int
    ): DiscoverResponse {

        return api.discoverUsers(
            page = page,
            limit = limit
        ).data
    }

    suspend fun createInteraction(
        targetId: Int,
        liked: Boolean
    ): InteractionResultData {

        return api.createInteraction(
            InteractionRequest(
                target_id = targetId,
                action_type = if (liked) "LIKE" else "PASS"
            )
        ).data
    }

    suspend fun getReceivedRequests(page: Int, limit: Int): DiscoverResponse {
        return api.getReceivedRequests(page = page, limit = limit).data
    }

    suspend fun getSentRequests(page: Int, limit: Int): DiscoverResponse {
        return api.getSentRequests(page = page, limit = limit).data
    }

    suspend fun getOnboardingStatus(): Boolean {
        return api.getOnboardingStatus().data.isBeginner
    }

    suspend fun completeOnboarding() {
        api.completeOnboarding()
    }
}