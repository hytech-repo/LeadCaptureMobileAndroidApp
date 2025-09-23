package com.eva.lead.capture.domain.repository

import com.eva.lead.capture.domain.model.ForgetPasswordRequest
import com.eva.lead.capture.domain.model.LoginRequest
import com.eva.lead.capture.domain.model.LoginResponse
import com.eva.lead.capture.utils.ResultWrapper

interface AuthRepository {

    suspend fun login(request: LoginRequest): ResultWrapper<LoginResponse>

    suspend fun forgetPassword(request: ForgetPasswordRequest): ResultWrapper<Any>

//    suspend fun logout(): LogoutResponse

}