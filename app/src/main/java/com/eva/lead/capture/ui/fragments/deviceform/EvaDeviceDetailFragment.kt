package com.eva.lead.capture.ui.fragments.deviceform

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eva.lead.capture.databinding.FragmentEvaDeviceDetailBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment

class EvaDeviceDetailFragment :
    BaseFragment<FragmentEvaDeviceDetailBinding, EvaDeviceDetailViewModel>(EvaDeviceDetailViewModel::class.java) {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaDeviceDetailFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaDeviceDetailBinding {
        return FragmentEvaDeviceDetailBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
        this.initListener()
    }

    private fun initView() {

    }

    private fun initListener() {
        binding.saveButton.setOnClickListener {
            navigateToOtherScreen()
        }
    }

    private fun navigateToOtherScreen() {
        val intent = Intent(mContext, EventHostActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mContext.startActivity(intent)
        requireActivity().overridePendingTransition(0, 0)
    }

    companion object {
        fun newInstance() = EvaDeviceDetailFragment()
    }
}