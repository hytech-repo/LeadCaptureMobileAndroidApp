package com.eva.lead.capture.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.jvm.java

class AppViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val constructor = modelClass.getConstructor(Context::class.java)
        return constructor.newInstance(context)
    }
}


//class EvaViewModelFactory<T : ViewModel>(
//    private val creator: () -> T
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return creator() as T
//    }
//}