package com.eva.lead.capture.ui.fragments.leadlist

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaAddLeadBinding
import com.eva.lead.capture.databinding.FragmentEvaLeadListBinding
import com.eva.lead.capture.ui.base.BaseFragment

class EvaLeadListFragment : BaseFragment<FragmentEvaLeadListBinding, EvaLeadListViewModel>(EvaLeadListViewModel::class.java) {
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaLeadListFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaLeadListBinding {
        return FragmentEvaLeadListBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Lead list"

    }


    companion object {
        fun newInstance() = EvaLeadListFragment()
    }
}