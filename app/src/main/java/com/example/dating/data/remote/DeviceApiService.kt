package com.example.dating.data.remote

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import com.example.dating.ui.chat.ApiResponse
import retrofit2.http.DELETE

@Serializable
data class RegisterDeviceRequest(

    @SerialName("device_token")
    val deviceToken: String,

    @SerialName("device_type")
    val deviceType: String
)

interface DeviceApiService {

    @POST("api/devices/register")
    suspend fun registerDevice(
        @Body request: RegisterDeviceRequest
    ): ApiResponse<Unit>

    @POST("api/devices/unregister")
    suspend fun unregisterDevice(
        @Body request: RegisterDeviceRequest
    ): ApiResponse<Unit>
}