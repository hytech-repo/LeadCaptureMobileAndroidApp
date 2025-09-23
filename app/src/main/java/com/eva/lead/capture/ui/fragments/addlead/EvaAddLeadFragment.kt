package com.eva.lead.capture.ui.fragments.addlead

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eva.lead.capture.databinding.FragmentEvaAddLeadBinding
import com.eva.lead.capture.ui.base.BaseFragment

class EvaAddLeadFragment :
    BaseFragment<FragmentEvaAddLeadBinding, EvaAddLeadViewModel>(EvaAddLeadViewModel::class.java) {

    companion object {
        fun newInstance() = EvaAddLeadFragment()
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAddLeadBinding {
        return FragmentEvaAddLeadBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
    }
}