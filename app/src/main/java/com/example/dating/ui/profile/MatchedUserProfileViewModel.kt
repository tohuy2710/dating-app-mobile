package com.example.dating.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.model.User
import com.example.dating.data.remote.UserApiService
import com.example.dating.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MatchedProfileUiState {
    data object Loading : MatchedProfileUiState()
    data class Success(val user: User) : MatchedProfileUiState()
    data class Error(val message: String) : MatchedProfileUiState()
}

class MatchedUserProfileViewModel(
    private val userId: Int
) : ViewModel() {

    private val userApiService: UserApiService by lazy {
        RetrofitClient.retrofitInstance.create(UserApiService::class.java)
    }

    private val repository: UserRepository by lazy {
        UserRepository(userApiService)
    }

    private val _uiState = MutableStateFlow<MatchedProfileUiState>(MatchedProfileUiState.Loading)
    val uiState: StateFlow<MatchedProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = MatchedProfileUiState.Loading
            try {
                val user = repository.getUserById(userId)
                _uiState.value = MatchedProfileUiState.Success(user)
            } catch (e: Exception) {
                android.util.Log.e("MatchedUserProfile", "Error loading user details", e)
                _uiState.value = MatchedProfileUiState.Error(e.message ?: "Failed to load user profile")
            }
        }
    }
}

class MatchedUserProfileViewModelFactory(
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchedUserProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MatchedUserProfileViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}