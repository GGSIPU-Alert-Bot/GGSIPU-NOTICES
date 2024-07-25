package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.Notice
import retrofit2.http.GET

interface ApiService {
    @GET("/notices/latest")
    suspend fun getNotices(): List<Notice>
}
