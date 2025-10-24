package com.eva.lead.capture.ui.fragments.appointment

import android.content.Context
import androidx.lifecycle.ViewModel
import com.eva.lead.capture.domain.model.entity.Appointment
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class EvaAppointmentViewModel(mcontext: Context) : BaseViewModel(mcontext) {

    fun getAllApointments(): Flow<List<Appointment>?> {
        return repositoryDb.getAllApointments()
    }

}