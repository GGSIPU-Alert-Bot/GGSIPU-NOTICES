package com.falcon.ggsipunotices.repository

import com.falcon.ggsipunotices.network.ApiHelper
import javax.inject.Inject

class NoticeRepository @Inject constructor(private val apiHelper: ApiHelper) {
    suspend fun getNotices() = apiHelper.getNotices()
}
