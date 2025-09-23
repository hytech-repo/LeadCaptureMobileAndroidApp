package com.eva.lead.capture.domain.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class ForgetPasswordRequest(
    @SerializedName("email")
    val email: String?,
    @SerializedName("event_id")
    val eventId: String?
)

data class LoginResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("first_name")
    val firstName: String?,

    @SerializedName("last_name")
    val lastName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)
