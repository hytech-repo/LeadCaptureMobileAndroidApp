package com.eva.lead.capture.data.remote

import com.eva.lead.capture.domain.model.AppException
import com.eva.lead.capture.domain.model.WebServiceFailure
import com.eva.lead.capture.utils.ResultWrapper
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

suspend fun <T> safeApiCall(
    apiCall: suspend () -> ApiResponse<T>
): ResultWrapper<T> {
    return try {
        val response = apiCall()
        if (response.status) {
            ResultWrapper.Success(response.data)
        } else {
            ResultWrapper.Error(
                AppException(
                    response.message ?: "Unknown API error",
                    response.statusCode
                )
            )
        }
    } catch (e: Exception) {
        when {
            e is HttpException -> ResultWrapper.Error(parseHttpException(e))
            e is SocketTimeoutException ->  ResultWrapper.Error(WebServiceFailure.NetworkTimeOutFailure())
            e is ConnectException ->  ResultWrapper.Error(WebServiceFailure.ConnectionFailure())
            e is JsonSyntaxException ->  ResultWrapper.Error(WebServiceFailure.NetworkDataFailure())
            e is UnknownHostException -> ResultWrapper.Error(WebServiceFailure.NoNetworkFailure())
            e is IllegalArgumentException -> ResultWrapper.Error(WebServiceFailure.InvalidRequestFailure())
            else ->  ResultWrapper.Error(WebServiceFailure.UnknownNetworkFailure())
        }


    }
}

fun parseHttpException(e: HttpException): AppException {
    val errorBody = e.response()?.errorBody()?.string()
    val message = try {
        val apiError = Gson().fromJson(errorBody, ApiResponse::class.java)
        apiError.message ?: e.message()
    } catch (ex: Exception) {
        e.message()
    }
    return AppException(message ?: "Unknown error", e.code())
}