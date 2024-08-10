package com.falcon.ggsipunotices.repository

import com.falcon.ggsipunotices.model.FcmCollegePreferenceRequest
import com.falcon.ggsipunotices.model.FcmPreferenceRequest
import com.falcon.ggsipunotices.network.ApiHelper
import com.falcon.ggsipunotices.network.FcmApiHelper
import javax.inject.Inject

class NoticeRepository @Inject constructor(private val apiHelper: ApiHelper) {
    suspend fun getNotices() = apiHelper.getNotices()
}

class FcmPreferenceRepository @Inject constructor(private val fcmApiHelper: FcmApiHelper) {
    suspend fun sendCollegePreference(deviceId: String, preference: FcmCollegePreferenceRequest) = fcmApiHelper.sendCollegePreference(deviceId, preference)
    suspend fun sendFcmPreference(deviceId: String, preference: FcmPreferenceRequest) = fcmApiHelper.sendFcmPreference(deviceId, preference)
}