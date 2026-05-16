package com.example.dating.data.repository

import com.example.dating.data.model.*
import com.example.dating.data.remote.MatchingApiService
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface MatchingRepository {
    suspend fun sendLike(userId: Int): InteractionData
    suspend fun sendPass(userId: Int): InteractionData
    suspend fun getPendingReceived(page: Int = 1): InteractionListResponse
    suspend fun acceptInteraction(interactionId: Int): MatchData
    suspend fun rejectInteraction(interactionId: Int): InteractionData
    suspend fun getInteractionStatus(userId: Int): InteractionStatusResponse
    suspend fun getDiscoveryUsers(page: Int = 1, limit: Int = 10): DiscoveryResponse
    suspend fun getNextDiscoveryUser(limit: Int = 10): User?
    suspend fun prefetchDiscoveryUsersIfNeeded(limit: Int = 10, threshold: Int = 5)
    suspend fun resetDiscoveryFeed(limit: Int = 10)
    suspend fun getMatches(page: Int = 1): MatchListResponse
    suspend fun getMatchDetail(matchId: Int, page: Int = 1): MatchDetailResponse
    suspend fun sendMessage(matchId: Int, content: String): MessageData
    suspend fun unmatch(matchId: Int)
}

class NetworkMatchingRepository(
    private val matchingApiService: MatchingApiService
) : MatchingRepository {

    private val discoveryMutex = Mutex()
    private val discoveryQueue = ArrayDeque<User>()
    private var nextDiscoveryPage = 1
    private var totalDiscoveryPages = 1
    private var hasLoadedDiscoveryFeed = false

    override suspend fun sendLike(userId: Int): InteractionData {
        val response = matchingApiService.sendInteraction(
            userId, 
            InteractionRequest(actionType = "LIKE", interactionMode = "traditional")
        )
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun sendPass(userId: Int): InteractionData {
        val response = matchingApiService.sendInteraction(
            userId, 
            InteractionRequest(actionType = "PASS", interactionMode = "traditional")
        )
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun getPendingReceived(page: Int): InteractionListResponse {
        val response = matchingApiService.getPendingReceived(page = page)
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun acceptInteraction(interactionId: Int): MatchData {
        val response = matchingApiService.acceptInteraction(interactionId)
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun rejectInteraction(interactionId: Int): InteractionData {
        val response = matchingApiService.rejectInteraction(interactionId)
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun getInteractionStatus(userId: Int): InteractionStatusResponse {
        val response = matchingApiService.getInteractionStatus(userId)
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun getDiscoveryUsers(page: Int, limit: Int): DiscoveryResponse {
        val response = matchingApiService.getDiscoveryUsers(page, limit)
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun getNextDiscoveryUser(limit: Int): User? {
        return discoveryMutex.withLock {
            ensureDiscoveryCache(limit)
            val nextUser = discoveryQueue.removeFirstOrNull()
            if (discoveryQueue.size <= 5) {
                fetchAndAppendDiscoveryPage(limit)
            }
            nextUser
        }
    }

    override suspend fun prefetchDiscoveryUsersIfNeeded(limit: Int, threshold: Int) {
        discoveryMutex.withLock {
            ensureDiscoveryCache(limit)
            if (discoveryQueue.size <= threshold) {
                fetchAndAppendDiscoveryPage(limit)
            }
        }
    }

    override suspend fun resetDiscoveryFeed(limit: Int) {
        discoveryMutex.withLock {
            discoveryQueue.clear()
            nextDiscoveryPage = 1
            totalDiscoveryPages = 1
            hasLoadedDiscoveryFeed = false
            ensureDiscoveryCache(limit)
        }
    }

    override suspend fun getMatches(page: Int): MatchListResponse {
        val response = matchingApiService.getMatches(page = page)
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun getMatchDetail(matchId: Int, page: Int): MatchDetailResponse {
        val response = matchingApiService.getMatchDetail(matchId, page = page)
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun sendMessage(matchId: Int, content: String): MessageData {
        val response = matchingApiService.sendMessage(matchId, SendMessageRequest(content))
        return response.data ?: throw Exception(response.message)
    }

    override suspend fun unmatch(matchId: Int) {
        val response = matchingApiService.unmatch(matchId)
        if (!response.success) throw Exception(response.message)
    }

    private suspend fun ensureDiscoveryCache(limit: Int) {
        if (!hasLoadedDiscoveryFeed || discoveryQueue.isEmpty()) {
            fetchAndAppendDiscoveryPage(limit)
        }
    }

    private suspend fun fetchAndAppendDiscoveryPage(limit: Int) {
        val response = matchingApiService.getDiscoveryUsers(nextDiscoveryPage, limit)
        val data = response.data ?: throw Exception(response.message)

        if (data.users.isNotEmpty()) {
            discoveryQueue.addAll(data.users)
        }

        totalDiscoveryPages = data.pagination.totalPages.coerceAtLeast(1)
        nextDiscoveryPage = if (nextDiscoveryPage < totalDiscoveryPages) {
            nextDiscoveryPage + 1
        } else {
            1
        }
        hasLoadedDiscoveryFeed = true
    }
}
