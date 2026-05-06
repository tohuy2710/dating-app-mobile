/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dating.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object (DAO) for Token database operations.
 */
@Dao
interface TokenDao {
    
    /**
     * Insert a new token into the database.
     * If a token with the same id exists, it replaces the existing one.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: TokenEntity)

    /**
     * Get the latest token from the database.
     * Returns the most recently created token.
     */
    @Query("SELECT * FROM tokens ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestToken(): TokenEntity?

    /**
     * Get all tokens from the database.
     */
    @Query("SELECT * FROM tokens ORDER BY createdAt DESC")
    suspend fun getAllTokens(): List<TokenEntity>

    /**
     * Update an existing token.
     */
    @Update
    suspend fun updateToken(token: TokenEntity)

    /**
     * Delete a specific token by ID.
     */
    @Query("DELETE FROM tokens WHERE id = :tokenId")
    suspend fun deleteToken(tokenId: Int)

    /**
     * Delete all tokens from the database.
     */
    @Query("DELETE FROM tokens")
    suspend fun deleteAllTokens()

    /**
     * Get token count.
     */
    @Query("SELECT COUNT(*) FROM tokens")
    suspend fun getTokenCount(): Int
}
