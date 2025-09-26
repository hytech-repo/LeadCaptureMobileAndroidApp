package com.eva.lead.capture.ui.fragments.home

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaHomeBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaHomeFragment :
    BaseFragment<FragmentEvaHomeBinding, EvaHomeViewModel>(EvaHomeViewModel::class.java) {
    private lateinit var mContext: Context
    private var leadList: List<EvaLeadData>? = mutableListOf()

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
        this.fetchLeadInfo()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
    }

    private fun fetchLeadInfo() {
        lifecycleScope.launch {
            leadList = viewModel.getLeadList().firstOrNull()
            if (leadList != null) {
                showLeadInfoDetailOnUi(leadList!!)
            }
        }
    }

    private fun showLeadInfoDetailOnUi(leadInfo: List<EvaLeadData>) {
        val colorRes =
            if (leadInfo.isNotEmpty()) R.color.color_lime_green else R.color.status_yellow
        val color = ContextCompat.getColor(mContext, colorRes)
        val bgColor = ColorUtils.setAlphaComponent(color, 15)
        binding.ivGroupIcon.imageTintList = ColorStateList.valueOf(color)
        binding.ivGroupIcon.backgroundTintList = ColorStateList.valueOf(bgColor)
        binding.tvLeadCount.setTextColor(color)
        if (leadInfo.isNotEmpty()) {
            binding.tvLeadCount.text = "${leadInfo.size}"
            binding.tvNoLead.text = mContext.getString(R.string.doing_good_msg)
        } else {
            binding.tvNoLead.text = mContext.getString(R.string.no_lead_yet)
        }
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Home"
    }

    private fun initListener() {
        binding.cvTotalLead.setOnClickListener {
            if (!leadList.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_homeFragment_to_evaLeadListFragment)
            }
        }
        binding.incToolbar.ivUserImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_evaUserProfileFragment)
        }
    }
}