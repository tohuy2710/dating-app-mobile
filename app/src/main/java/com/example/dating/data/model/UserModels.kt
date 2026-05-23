package com.example.dating.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(

    @SerialName("user_id")
    val userId: Int,

    val email: String,

    @SerialName("full_name")
    val fullName: String,

    @SerialName("birth_date")
    val birthDate: String? = null,

    val gender: String? = null,

    val bio: String? = null,

    @SerialName("default_mode")
    val defaultMode: String = "traditional",

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    val preferences: UserPreferences? = null,

    val photos: List<UserPhoto> = emptyList()
) {
    val avatarUrl: String
        get() = photos.find { it.isPrimary }?.imageUrl
            ?: photos.firstOrNull()?.imageUrl
            ?: ""
}

@Serializable
data class UserPreferences(

    @SerialName("preference_id")
    val preferenceId: Int,

    @SerialName("target_gender")
    val targetGender: String? = null,

    @SerialName("min_age")
    val minAge: Int = 18,

    @SerialName("max_age")
    val maxAge: Int = 99,

    @SerialName("max_distance_km")
    val maxDistanceKm: Int = 50,

    @SerialName("anonymous_interests")
    val anonymousInterests: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class UserPhoto(

    @SerialName("photo_id")
    val photoId: Int,

    @SerialName("image_url")
    val imageUrl: String,

    @SerialName("is_primary")
    val isPrimary: Boolean = false,

    @SerialName("display_order")
    val displayOrder: Int? = null,

    @SerialName("created_at")
    val createdAt: String? = null
)