package com.example.dating.ui.anonymous

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf // Import thêm cái này
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dating.core.network.RetrofitClient
import com.example.dating.core.socket.SocketManager
import com.example.dating.data.remote.MatchesApiService
import com.example.dating.data.repository.AnonymousRepository
import com.example.dating.ui.chat.MatchDetailResponseData
import com.google.gson.Gson // Import Gson để parse JSON
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject

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

    var remainingSeconds by mutableIntStateOf(30)
        private set

    init {
        observeSocketEvents()
    }

    private fun observeSocketEvents() {
        viewModelScope.launch {
            SocketManager.anonymousMatchEvent.collect { data ->
                handleSocketMatch(data)
            }
        }
    }

    private fun handleSocketMatch(data: JSONObject) {
        try {
            val matchScore = data.getInt("matchScore")
            val matchObj = data.getJSONObject("match")
            Log.d("SOCKET", "Match Score: $matchScore")
            Log.d("SOCKET", "Match Object: $matchObj")

            val json = Json { ignoreUnknownKeys = true }
            val matchData = json.decodeFromString<MatchDetailResponseData>(matchObj.toString())
            Log.d("SOCKET", "Match Data: $matchData")


            countdownJob?.cancel()

            anonymousUiState = AnonymousUiState.Matched(
                match = matchData,
                matchScore = matchScore
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startAnonymousMatching() {
        anonymousUiState = AnonymousUiState.Searching
        remainingSeconds = 30
        startCountdown()

        viewModelScope.launch {
            try {
                val response = anonymousRepository.anonymousMatch()

                when (response.status) {
                    "matched" -> {
                        countdownJob?.cancel()
                        anonymousUiState = AnonymousUiState.Matched(
                            match = response.match,
                            matchScore = response.matchScore
                        )
                    }
                    "queued" -> {
                        anonymousUiState = AnonymousUiState.Queued(
                            queueId = response.queueId ?: -1,
                            message = response.message
                        )
                    }
                    else -> {
                        anonymousUiState = AnonymousUiState.Error("Unknown response state")
                    }
                }
            } catch (e: Exception) {
                countdownJob?.cancel()
                anonymousUiState = AnonymousUiState.Error(e.message ?: "Anonymous matching failed")
            }
        }
    }

    fun cancelMatching() {
        viewModelScope.launch {
            try {
                anonymousRepository.cancelAnonymousQueue()
                resetState()
                Log.d("AnonymousMatch", "Đã hủy tìm kiếm ẩn danh thành công")
            } catch (e: Exception) {
                Log.e("AnonymousMatch", "Lỗi khi hủy tìm kiếm: ${e.message}")
                resetState()
            }
        }
    }

    fun resetState() {
        countdownJob?.cancel()
        remainingSeconds = 30
        anonymousUiState = AnonymousUiState.Idle
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (remainingSeconds > 0) {
                delay(1000)
                remainingSeconds--
            }

            // Tránh đè state nếu socket/API vừa trả về đúng lúc giây thứ 0
            if (remainingSeconds == 0 && anonymousUiState !is AnonymousUiState.Matched) {
                anonymousUiState = AnonymousUiState.Timeout
            }
        }
    }
}

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