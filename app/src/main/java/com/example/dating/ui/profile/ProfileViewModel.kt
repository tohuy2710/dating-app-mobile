package com.example.dating.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dating.DatingApplication
import com.example.dating.data.model.User
import com.example.dating.data.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var profileUiState: ProfileUiState by mutableStateOf(ProfileUiState.Loading)
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            profileUiState = ProfileUiState.Loading
            profileUiState = try {
                ProfileUiState.Success(authRepository.getMe())
            } catch (e: IOException) {
                ProfileUiState.Error("Network error. Please check your connection.")
            } catch (e: HttpException) {
                ProfileUiState.Error("Unable to load profile right now.")
            } catch (e: Exception) {
                ProfileUiState.Error(e.message ?: "Unable to load profile.")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as DatingApplication
                ProfileViewModel(
                    authRepository = application.container.authRepository
                )
            }
        }
    }
}
