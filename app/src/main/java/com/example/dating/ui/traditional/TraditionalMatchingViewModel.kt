package com.example.dating.ui.traditional

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dating.DatingApplication
import com.example.dating.data.model.*
import com.example.dating.data.repository.MatchingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MatchingUiState {
    data object Idle : MatchingUiState
    data object Loading : MatchingUiState
    data class Success(val message: String) : MatchingUiState
    data class Error(val message: String) : MatchingUiState
}

sealed interface InteractionListUiState {
    data object Loading : InteractionListUiState
    data class Success(val requests: List<InteractionData>) : InteractionListUiState
    data class Error(val message: String) : InteractionListUiState
}

class TraditionalMatchingViewModel(
    private val matchingRepository: MatchingRepository
) : ViewModel() {

    private val discoveryPageLimit = 10
    private val discoveryPrefetchThreshold = 5

    private val _uiState = MutableStateFlow<MatchingUiState>(MatchingUiState.Idle)
    val uiState: StateFlow<MatchingUiState> = _uiState.asStateFlow()

    private val _pendingReceived = MutableStateFlow<InteractionListUiState>(InteractionListUiState.Loading)
    val pendingReceived: StateFlow<InteractionListUiState> = _pendingReceived.asStateFlow()

    private val _matches = MutableStateFlow<List<MatchData>>(emptyList())
    val matches: StateFlow<List<MatchData>> = _matches.asStateFlow()

    private val _currentDiscoveryProfile = MutableStateFlow<User?>(null)
    val currentDiscoveryProfile: StateFlow<User?> = _currentDiscoveryProfile.asStateFlow()

    private val _currentDiscoveryTargetId = MutableStateFlow<Int?>(null)
    val currentDiscoveryTargetId: StateFlow<Int?> = _currentDiscoveryTargetId.asStateFlow()

    private var isLoadingDiscoveryProfile = false

    init {
        loadDiscoveryProfiles(reset = true)
    }

    fun loadDiscoveryProfiles(reset: Boolean = false) {
        if (isLoadingDiscoveryProfile) return

        viewModelScope.launch {
            isLoadingDiscoveryProfile = true
            if (_currentDiscoveryProfile.value == null) {
                _uiState.value = MatchingUiState.Loading
            }
            try {
                if (reset) {
                    matchingRepository.resetDiscoveryFeed(limit = discoveryPageLimit)
                }
                val nextProfile = matchingRepository.getNextDiscoveryUser(limit = discoveryPageLimit)
                _currentDiscoveryProfile.value = nextProfile
                _currentDiscoveryTargetId.value = nextProfile?.userId
                if (nextProfile == null) {
                    _uiState.value = MatchingUiState.Error("No profiles available right now")
                } else {
                    _uiState.value = MatchingUiState.Idle
                    matchingRepository.prefetchDiscoveryUsersIfNeeded(
                        limit = discoveryPageLimit,
                        threshold = discoveryPrefetchThreshold
                    )
                }
            } catch (e: Exception) {
                _uiState.value = MatchingUiState.Error(e.message ?: "Failed to load profiles")
            } finally {
                isLoadingDiscoveryProfile = false
            }
        }
    }

    private fun advanceToNextProfile() {
        _currentDiscoveryProfile.value = null
        _currentDiscoveryTargetId.value = null
        loadDiscoveryProfiles(reset = false)
    }

    fun likeCurrentDiscoveryUser(targetUserId: Int? = _currentDiscoveryTargetId.value) {
        viewModelScope.launch {
            val userId = targetUserId
            if (userId == null) {
                _uiState.value = MatchingUiState.Error("No active profile selected")
                return@launch
            }
            _uiState.value = MatchingUiState.Loading
            try {
                matchingRepository.sendLike(userId)
                advanceToNextProfile()
            } catch (e: Exception) {
                _uiState.value = MatchingUiState.Error(e.message ?: "Failed to like user")
            }
        }
    }

    fun passCurrentDiscoveryUser(targetUserId: Int? = _currentDiscoveryTargetId.value) {
        viewModelScope.launch {
            val userId = targetUserId
            if (userId == null) {
                _uiState.value = MatchingUiState.Error("No active profile selected")
                return@launch
            }
            _uiState.value = MatchingUiState.Loading
            try {
                matchingRepository.sendPass(userId)
                advanceToNextProfile()
            } catch (e: Exception) {
                _uiState.value = MatchingUiState.Error(e.message ?: "Failed to pass user")
            }
        }
    }

    fun loadPendingReceived() {
        viewModelScope.launch {
            _pendingReceived.value = InteractionListUiState.Loading
            try {
                val response = matchingRepository.getPendingReceived()
                _pendingReceived.value = InteractionListUiState.Success(response.requests)
            } catch (e: Exception) {
                _pendingReceived.value = InteractionListUiState.Error(e.message ?: "Failed to load requests")
            }
        }
    }

    fun acceptInteraction(interactionId: Int) {
        viewModelScope.launch {
            _uiState.value = MatchingUiState.Loading
            try {
                matchingRepository.acceptInteraction(interactionId)
                _uiState.value = MatchingUiState.Success("Match created!")
                loadPendingReceived()
                loadMatches()
            } catch (e: Exception) {
                _uiState.value = MatchingUiState.Error(e.message ?: "Failed to accept")
            }
        }
    }

    fun rejectInteraction(interactionId: Int) {
        viewModelScope.launch {
            _uiState.value = MatchingUiState.Loading
            try {
                matchingRepository.rejectInteraction(interactionId)
                _uiState.value = MatchingUiState.Success("Interaction rejected")
                loadPendingReceived()
            } catch (e: Exception) {
                _uiState.value = MatchingUiState.Error(e.message ?: "Failed to reject")
            }
        }
    }

    fun loadMatches() {
        viewModelScope.launch {
            try {
                val response = matchingRepository.getMatches()
                _matches.value = response.matches
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Factory for [TraditionalMatchingViewModel] that takes [MatchingRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as DatingApplication)
                val matchingRepository = application.container.matchingRepository
                TraditionalMatchingViewModel(matchingRepository = matchingRepository)
            }
        }
    }
}
