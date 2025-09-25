package com.eva.lead.capture.ui.fragments.appointment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eva.lead.capture.databinding.FragmentEvaAppointmentBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment

class EvaAppointmentFragment :
    BaseFragment<FragmentEvaAppointmentBinding, EvaAppointmentViewModel>(EvaAppointmentViewModel::class.java) {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaAppointmentFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAppointmentBinding {
        return FragmentEvaAppointmentBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
    }

    companion object {
        fun newInstance() = EvaAppointmentFragment()
    }
}