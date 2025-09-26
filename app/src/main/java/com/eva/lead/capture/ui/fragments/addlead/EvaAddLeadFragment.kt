package com.eva.lead.capture.ui.fragments.addlead

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaAddLeadBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment

class EvaAddLeadFragment :
    BaseFragment<FragmentEvaAddLeadBinding, EvaAddLeadViewModel>(EvaAddLeadViewModel::class.java) {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaAddLeadFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAddLeadBinding {
        return FragmentEvaAddLeadBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
        this.initView()
        this.initListener()
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Add Lead"
    }

    private fun initListener() {
        binding.cvAddManual.setOnClickListener {
            findNavController().navigate(R.id.action_evaAddLeadFragment_to_evaAddManualLead)
        }
        binding.cvQrCode.setOnClickListener {
            findNavController().navigate(R.id.action_evaAddLeadFragment_to_evaCameraFragment)
        }
        binding.cvBusinessCard.setOnClickListener {
            findNavController().navigate(R.id.action_evaAddLeadFragment_to_evaCameraFragment)
        }
        binding.incToolbar.ivUserImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_evaUserProfileFragment)
        }
    }

    companion object {
        fun newInstance() = EvaAddLeadFragment()
    }
}