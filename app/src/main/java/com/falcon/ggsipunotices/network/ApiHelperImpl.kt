package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.Notice
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override suspend fun getNotices(): List<Notice> = apiService.getNotices()

    override suspend fun sendFcmToken(deviceId: String, token: FcmTokenRequest): Response<Unit> =
        apiService.sendFcmToken(deviceId, token)
}
