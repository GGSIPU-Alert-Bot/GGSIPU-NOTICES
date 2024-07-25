package com.falcon.ggsipunotices.network

import com.falcon.ggsipunotices.model.Notice

interface ApiHelper {
    suspend fun getNotices(): List<Notice>
}
