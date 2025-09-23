package com.eva.lead.capture.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaHomeBinding
import com.eva.lead.capture.ui.base.BaseFragment

class EvaHomeFragment :
    BaseFragment<FragmentEvaHomeBinding, EvaHomeViewModel>(EvaHomeViewModel::class.java) {


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaHomeBinding {
        return FragmentEvaHomeBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
    }
}