package com.eva.lead.capture.data.remote

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success", alternate = ["status"])
    val status: Boolean,

    @SerializedName("statusCode")
    var statusCode: Int?,

    @SerializedName("data")
    val data: T?,

    @SerializedName("message")
    val message: String?
)
