package com.eva.lead.capture.ui.fragments.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaHomeBinding
import com.eva.lead.capture.ui.base.BaseFragment

class EvaHomeFragment :
    BaseFragment<FragmentEvaHomeBinding, EvaHomeViewModel>(EvaHomeViewModel::class.java) {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaHomeFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaHomeBinding {
        return FragmentEvaHomeBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
        this.initListener()
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Home"
    }

    private fun initListener() {
        binding.cvTotalLead.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_evaLeadListFragment)
        }
    }
}