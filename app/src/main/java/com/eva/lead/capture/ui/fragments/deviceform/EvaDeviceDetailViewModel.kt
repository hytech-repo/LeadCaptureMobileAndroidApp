package com.eva.lead.capture.ui.fragments.deviceform

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaDeviceDetailViewModel(mcontext: Context) : BaseViewModel(mcontext) {

    fun checkExhibitor(leadCode: String): Flow<Exhibitor?> {
        return repositoryDb.getExhibitorByLeadCode(leadCode)
    }

    fun updateExhibitor(exhibitor: Exhibitor?, callback: () -> Unit) {
        viewModelScope.launch {
            repositoryDb.updateExhibitor(exhibitor!!)
            callback()
        }
    }

}