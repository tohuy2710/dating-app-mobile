package com.example.dating.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dating.core.network.RetrofitClient
import com.example.dating.data.model.User
import com.example.dating.data.remote.UserApiService
import com.example.dating.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileUiState {

    data object Loading : ProfileUiState()

    data class Success(
        val user: User
    ) : ProfileUiState()

    data class Error(
        val message: String
    ) : ProfileUiState()
}

class ProfileViewModel : ViewModel() {

    private val userApiService: UserApiService by lazy {

        RetrofitClient.retrofitInstance.create(
            UserApiService::class.java
        )
    }

    private val repository: UserRepository by lazy {

        UserRepository(userApiService)
    }

    private val _uiState =
        MutableStateFlow<ProfileUiState>(
            ProfileUiState.Loading
        )

    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    init {
        getCurrentUser()
    }

    fun getCurrentUser() {

        viewModelScope.launch {

            _uiState.value =
                ProfileUiState.Loading

            try {

                val user =
                    repository.getCurrentUser()

                _uiState.value =
                    ProfileUiState.Success(user)

            } catch (e: Exception) {

                _uiState.value =
                    ProfileUiState.Error(
                        e.message ?: "Unknown error"
                    )
            }
        }
    }

    fun updateAnonymousInterests(
        interests: List<String>
    ) {

        viewModelScope.launch {

            try {

                repository.updateAnonymousInterests(
                    interests.joinToString(",")
                )

                getCurrentUser()

            } catch (_: Exception) {

            }
        }
    }
}