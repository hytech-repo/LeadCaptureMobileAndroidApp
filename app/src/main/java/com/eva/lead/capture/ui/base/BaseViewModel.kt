package com.eva.lead.capture.ui.base

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.data.local.AppDatabase
import com.eva.lead.capture.data.repository.AppDbRepositoryImpl
import com.eva.lead.capture.domain.repository.AppDbRepository
import com.eva.lead.capture.utils.AppLogger
import com.eva.lead.capture.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseViewModel(mcontext: Context) : ViewModel() {

    protected val log: AppLogger = AppLogger(mcontext)
    protected var repositoryDb: AppDbRepository

    protected val _failure: SingleLiveEvent<Exception> by lazy { SingleLiveEvent() }

    val failure: LiveData<Exception> get() = _failure

    init {
        val userDao = AppDatabase.getInstance(mcontext)
        repositoryDb = AppDbRepositoryImpl(userDao)
    }

    protected fun launchViewModelScope(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                block()
            } catch (e: Exception) {
                _failure.postValue(e)
            }
        }
    }

}