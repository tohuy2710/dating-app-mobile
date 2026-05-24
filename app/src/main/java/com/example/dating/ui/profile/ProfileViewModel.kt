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
import java.text.SimpleDateFormat
import java.util.Locale

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

                repository.updateAnonymousInterests(interests)
                getCurrentUser()

            } catch (e: Exception) {
                android.util.Log.e(
                    "PROFILE",
                    "Update interests failed",
                    e
                )
            }
        }
    }

    fun addPhoto(
        imageUrl: String
    ) {

        viewModelScope.launch {

            try {
                android.util.Log.d(
                    "PROFILE",
                    "Calling createPhoto"
                )

                repository.createPhoto(
                    imageUrl = imageUrl
                )

                android.util.Log.d(
                    "PROFILE",
                    "createPhoto success"
                )

                getCurrentUser()

            } catch (e: Exception) {
                android.util.Log.e(
                    "PROFILE",
                    "createPhoto failed",
                    e
                )
            }
        }
    }

    fun updateProfile(name: String, birthDate: String, bio: String) {
        viewModelScope.launch {
            try {
                // Đổi định dạng ngày từ dd/MM/yyyy (UI) sang yyyy-MM-dd (Database)
                val formattedDate = try {
                    if (birthDate.contains("/")) {
                        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = inputFormat.parse(birthDate)
                        date?.let { outputFormat.format(it) } ?: birthDate
                    } else {
                        birthDate
                    }
                } catch (e: Exception) {
                    birthDate
                }

                repository.updateProfile(name, formattedDate, bio)

                // Gọi API lấy lại thông tin user mới nhất sau khi update thành công
                getCurrentUser()

            } catch (e: Exception) {
                android.util.Log.e("PROFILE", "Update profile failed", e)
            }
        }
    }
}