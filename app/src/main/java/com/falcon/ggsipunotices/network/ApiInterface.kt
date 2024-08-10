package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.FcmCollegePreferenceRequest
import com.falcon.ggsipunotices.model.FcmPreferenceRequest
import com.falcon.ggsipunotices.model.FcmTokenRequest
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.model.PreferenceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/notices/latest")
    suspend fun getNotices(): List<Notice>
}

interface FcmApiService {
    @PUT("/api/devices/{deviceId}/fcm-token")
    suspend fun sendFcmToken(
        @Path("deviceId") deviceId: String,
        @Body token: FcmTokenRequest
    ): Response<Unit>

    @PUT("/api/devices/{deviceId}/preferences")
    suspend fun sendFcmPreference(
        @Path("deviceId") deviceId: String,
        @Body preference: FcmPreferenceRequest
    ): Response<PreferenceResponse>

    @PUT("/api/devices/{deviceId}/college-preference")
    suspend fun sendCollegePreference(
        @Path("deviceId") deviceId: String,
        @Body college: FcmCollegePreferenceRequest
    ): Response<Unit>
}