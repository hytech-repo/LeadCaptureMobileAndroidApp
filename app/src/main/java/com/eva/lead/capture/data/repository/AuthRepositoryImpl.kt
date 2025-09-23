package com.eva.lead.capture.data.repository

import com.eva.lead.capture.data.remote.ApiInterface
import com.eva.lead.capture.data.remote.safeApiCall
import com.eva.lead.capture.domain.model.ForgetPasswordRequest
import com.eva.lead.capture.domain.model.LoginRequest
import com.eva.lead.capture.domain.model.LoginResponse
import com.eva.lead.capture.domain.repository.AuthRepository
import com.eva.lead.capture.utils.ResultWrapper

class AuthRepositoryImpl(private val api: ApiInterface) : AuthRepository {


    override suspend fun login(request: LoginRequest): ResultWrapper<LoginResponse> {
        return safeApiCall { api.loginUser(request) }
    }

    override suspend fun forgetPassword(request: ForgetPasswordRequest): ResultWrapper<Any> {
        return safeApiCall { api.forgetPassword(request) }
    }

//    override suspend fun logout(): LogoutResponse {
//        TODO("Not yet implemented")
//    }

}