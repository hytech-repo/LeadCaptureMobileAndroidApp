package com.eva.lead.capture.ui.fragments.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaUserProfileBinding
import com.eva.lead.capture.ui.base.BaseFragment

class EvaUserProfileFragment :
    BaseFragment<FragmentEvaUserProfileBinding, EvaUserProfileViewModel>(EvaUserProfileViewModel::class.java) {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaUserProfileFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaUserProfileBinding {
        return FragmentEvaUserProfileBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
    }

    companion object {
        fun newInstance() = EvaUserProfileFragment()
    }
}