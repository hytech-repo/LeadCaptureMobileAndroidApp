package com.eva.lead.capture.ui.fragments.appointment

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.Appointment
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaAppointmentViewModel(mcontext: Context) : BaseViewModel(mcontext) {

    fun getAllApointments(): Flow<List<Appointment>?> {
        return repositoryDb.getAllApointments()
    }

    fun updateAppointment(appointment: Appointment, callback: (() -> Unit)? = null) {
        viewModelScope.launch {
            repositoryDb.updateAppointment(appointment)
            callback?.invoke()
        }
    }

}