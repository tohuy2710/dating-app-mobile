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

package com.example.dating.core.config

/**
 * Central configuration for API endpoints and environment settings.
 * 
 * Update API_BASE_URL to match your backend server:
 * - Development: http://localhost:5000/
 * - Android Emulator: http://10.0.2.2:5000/
 * - Physical Device: http://<your-machine-ip>:5000/
 */
object AppConfig {
    // API Configuration
//    const val API_BASE_URL = "http://localhost:5000/"
    
    // For Android Emulator, use:
//     const val API_BASE_URL = "http://10.0.2.2:5000/"
    
    // For physical device on same network, use your machine IP:
     const val API_BASE_URL = "http://192.168.1.19:5000/"
    
    // JWT Token Configuration
    // TODO: Replace with actual token from login endpoint when available
    const val MOCK_JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaWF0IjoxNzc5MDExNTg2LCJleHAiOjE3Nzk2MTYzODZ9.pGSrxHuDMDynARpXFUldRakjV7KKwRsPJVgYxx-X96k"
    
    // Timeouts (in seconds)
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}
