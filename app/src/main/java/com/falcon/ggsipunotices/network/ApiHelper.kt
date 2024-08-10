package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.FcmCollegePreferenceRequest
import com.falcon.ggsipunotices.model.FcmPreferenceRequest
import com.falcon.ggsipunotices.model.FcmTokenRequest
import com.falcon.ggsipunotices.model.Notice
import com.falcon.ggsipunotices.model.PreferenceResponse
import retrofit2.Response
import javax.inject.Inject

class ApiHelper @Inject constructor(private val apiService: ApiService) {
    suspend fun getNotices(): List<Notice> = apiService.getNotices()
}

class FcmApiHelper @Inject constructor(private val apiService: FcmApiService) {
    suspend fun sendFcmToken(deviceId: String, token: FcmTokenRequest): Response<Unit> =
        apiService.sendFcmToken(deviceId, token)

    suspend fun sendFcmPreference(deviceId: String, preference: FcmPreferenceRequest): Response<PreferenceResponse> =
        apiService.sendFcmPreference(deviceId, preference)

    suspend fun sendCollegePreference(deviceId: String, college: FcmCollegePreferenceRequest): Response<Unit> =
        apiService.sendCollegePreference(deviceId, college)
}