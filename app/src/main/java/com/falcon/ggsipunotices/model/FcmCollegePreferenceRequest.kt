package com.falcon.ggsipunotices.model

import com.google.gson.annotations.SerializedName

data class FcmCollegePreferenceRequest (
    @SerializedName("college")
    val college: String? = "all"
)
