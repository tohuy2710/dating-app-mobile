package com.example.dating.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PreferencesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferencesEntity)

    @Update
    suspend fun updatePreferences(preferences: UserPreferencesEntity)

    @Query("SELECT * FROM user_preferences WHERE user_id = :userId LIMIT 1")
    suspend fun getPreferencesForUser(userId: Int): UserPreferencesEntity?

    @Query("DELETE FROM user_preferences WHERE user_id = :userId")
    suspend fun deleteForUser(userId: Int)
}
