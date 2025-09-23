package com.eva.lead.capture.ui.fragments.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eva.lead.capture.databinding.FragmentEvaAppointmentBinding
import com.eva.lead.capture.ui.base.BaseFragment

class EvaAppointmentFragment :
    BaseFragment<FragmentEvaAppointmentBinding, EvaAppointmentViewModel>(EvaAppointmentViewModel::class.java) {

    companion object {
        fun newInstance() = EvaAppointmentFragment()
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAppointmentBinding {
        return FragmentEvaAppointmentBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
    }
}