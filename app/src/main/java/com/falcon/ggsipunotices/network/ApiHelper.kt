package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.Notice
import retrofit2.Response
import javax.inject.Inject

class ApiHelper @Inject constructor(private val apiService: ApiService) {
    suspend fun getNotices(): List<Notice> = apiService.getNotices()
}

class FcmApiHelper @Inject constructor(private val apiService: FcmApiService) {
    suspend fun sendFcmToken(deviceId: String, token: FcmTokenRequest): Response<Unit> =
        apiService.sendFcmToken(deviceId, token)
}