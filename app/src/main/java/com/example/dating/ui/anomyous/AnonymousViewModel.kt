package com.example.dating.ui.anonymous

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.remote.MatchesApiService
import com.example.dating.data.repository.AnonymousRepository
import com.example.dating.ui.chat.MatchDetailResponseData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AnonymousViewModel : ViewModel() {

    private val matchesApiService: MatchesApiService by lazy {
        RetrofitClient.retrofitInstance.create(
            MatchesApiService::class.java
        )
    }

    private val anonymousRepository: AnonymousRepository by lazy {
        AnonymousRepository(matchesApiService)
    }

    var anonymousUiState: AnonymousUiState by mutableStateOf(
        AnonymousUiState.Idle
    )
        private set

    private var countdownJob: Job? = null

    var remainingSeconds by mutableStateOf(30)
        private set

    /**
     * Start anonymous matching
     *
     * API:
     * POST /api/matches/anonymous
     */
    fun startAnonymousMatching() {

        anonymousUiState = AnonymousUiState.Searching

        remainingSeconds = 30

        startCountdown()

        viewModelScope.launch {

            try {

                val response =
                    anonymousRepository.anonymousMatch()

                when (response.status) {

                    "matched" -> {

                        countdownJob?.cancel()

                        anonymousUiState =
                            AnonymousUiState.Matched(
                                match = response.match,
                                matchScore = response.matchScore
                            )
                    }

                    "queued" -> {

                        anonymousUiState =
                            AnonymousUiState.Queued(
                                queueId = response.queueId ?: -1,
                                message = response.message
                            )
                    }

                    else -> {

                        anonymousUiState =
                            AnonymousUiState.Error(
                                "Unknown response state"
                            )
                    }
                }

            } catch (e: Exception) {

                countdownJob?.cancel()

                anonymousUiState =
                    AnonymousUiState.Error(
                        e.message ?: "Anonymous matching failed"
                    )
            }
        }
    }

    /**
     * Cancel matching
     */
    fun cancelMatching() {

        countdownJob?.cancel()

        anonymousUiState = AnonymousUiState.Idle
    }

    /**
     * Reset state
     */
    fun resetState() {

        countdownJob?.cancel()

        remainingSeconds = 30

        anonymousUiState = AnonymousUiState.Idle
    }

    /**
     * Countdown 30 seconds
     */
    private fun startCountdown() {

        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {

            while (remainingSeconds > 0) {

                delay(1000)

                remainingSeconds--
            }

            if (remainingSeconds == 0 &&
                anonymousUiState !is AnonymousUiState.Matched
            ) {

                anonymousUiState =
                    AnonymousUiState.Timeout
            }
        }
    }
}

/**
 * UI State for Anonymous Matching
 */
sealed class AnonymousUiState {

    data object Idle : AnonymousUiState()

    data object Searching : AnonymousUiState()

    data class Queued(
        val queueId: Int,
        val message: String?
    ) : AnonymousUiState()

    data class Matched(
        val match: MatchDetailResponseData?,
        val matchScore: Int?
    ) : AnonymousUiState()

    data object Timeout : AnonymousUiState()

    data class Error(
        val message: String
    ) : AnonymousUiState()
}