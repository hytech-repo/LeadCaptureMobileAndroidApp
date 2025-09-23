package com.eva.lead.capture.utils

/**
 *
 * Created by Laxmi Kant Joshi on 19/08/2025
 *
 * */

import com.eva.lead.capture.domain.model.AppException

sealed class ResultWrapper<out T> {
    data class Success<out T>(val data: T?) : ResultWrapper<T>()
    data class Error(val exception: AppException) : ResultWrapper<Nothing>()
    object Loading : ResultWrapper<Nothing>()
}