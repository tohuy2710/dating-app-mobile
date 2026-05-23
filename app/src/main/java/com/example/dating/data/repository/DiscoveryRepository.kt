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
}