package com.falcon.ggsipunotices.model

import com.google.gson.annotations.SerializedName

data class FcmPreferenceRequest (
    @SerializedName("preference")
    val preference: String? = "all"
)