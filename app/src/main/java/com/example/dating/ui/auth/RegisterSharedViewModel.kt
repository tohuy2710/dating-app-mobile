package com.example.dating.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.dating.data.model.RegisterRequest
import com.example.dating.data.model.RegisterUiState

class RegisterSharedViewModel : ViewModel() {

    var state by mutableStateOf(RegisterUiState())
        private set

    fun update(block: (RegisterUiState) -> RegisterUiState) {
        state = block(state)
    }

    fun setBasicInfo(
        email: String,
        password: String,
        fullName: String,
        birthDate: String,
        gender: String,
        bio: String,
        imageUrl: String?
    ) {
        update { current ->
            current.copy(
                email = email,
                password = password,
                fullName = fullName,
                birthDate = birthDate,
                gender = gender,
                bio = bio,
                imageUrl = imageUrl
            )
        }
    }

    fun setPreferences(
        interests: List<String>,
        targetGender: String?,
        minAge: Int?,
        maxAge: Int?,
        maxDistanceKm: Int?
    ) {
        update { current ->
            current.copy(
                interests = interests,
                targetGender = targetGender,
                minAge = minAge,
                maxAge = maxAge,
                maxDistanceKm = maxDistanceKm
            )
        }
    }

    fun toRegisterRequest() : RegisterRequest {
        return RegisterRequest(
            email = state.email,
            password = state.password,
            full_name = state.fullName,
            birth_date = state.birthDate,
            gender = state.gender,
            bio = state.bio,
            image_url = state.imageUrl,
            anonymous_interests = state.interests,
            target_gender = state.targetGender,
            min_age = state.minAge,
            max_age = state.maxAge,
            max_distance_km = state.maxDistanceKm
        )
    }

}