package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.Notice
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override suspend fun getNotices(): List<Notice> = apiService.getNotices()
}
