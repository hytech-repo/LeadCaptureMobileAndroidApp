package com.eva.lead.capture.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object ConnectivityObserver {
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    fun updateConnectionStatus(isConnected: Boolean) {
        _isConnected.postValue(isConnected)
    }
}