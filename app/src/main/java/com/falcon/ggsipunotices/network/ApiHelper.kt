package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.Notice
import retrofit2.Response

interface ApiHelper {
    suspend fun getNotices(): List<Notice>
    suspend fun sendFcmToken(deviceId: String, token: FcmTokenRequest): Response<Unit>
}
