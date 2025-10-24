package com.eva.lead.capture.ui.fragments.bookappointment

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.Appointment
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaBookAppointmentViewModel(val mcontext: Context) : BaseViewModel(mcontext) {

    fun getLeadList(): Flow<List<EvaLeadData>?> {
        return repositoryDb.getAllLeads()
    }

    fun saveLeadData(leadData: EvaLeadData) {
        viewModelScope.launch {
//            _loader.value = ResultWrapper.Loading
            repositoryDb.insertLead(leadData)
        }
    }

    fun saveAppointment(appointment: Appointment, callback: (() -> Unit)? = null) {
        viewModelScope.launch {
            repositoryDb.insertAppointment(appointment)
            callback?.invoke()
        }
    }

    fun updateAppointment(appointment: Appointment, callback: (() -> Unit)? = null) {
        viewModelScope.launch {
            repositoryDb.updateAppointment(appointment)
            callback?.invoke()
        }
    }
}