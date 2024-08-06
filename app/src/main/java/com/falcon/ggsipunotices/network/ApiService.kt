package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.Notice
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/notices/latest")
    suspend fun getNotices(): List<Notice>

    @PUT("/api/devices/{deviceId}/fcm-token")
    suspend fun sendFcmToken(@Path("deviceId") deviceId: String, @Body token: FcmTokenRequest): Response<Unit>
}
