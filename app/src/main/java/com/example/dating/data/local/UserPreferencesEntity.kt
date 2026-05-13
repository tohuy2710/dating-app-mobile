package com.example.dating.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preference_id")
    val preferenceId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "target_gender")
    val targetGender: String?,

    @ColumnInfo(name = "min_age")
    val minAge: Int?,

    @ColumnInfo(name = "max_age")
    val maxAge: Int?,

    @ColumnInfo(name = "max_distance_km")
    val maxDistanceKm: Int?,

    @ColumnInfo(name = "anonymous_interests")
    val anonymousInterests: String?
)
