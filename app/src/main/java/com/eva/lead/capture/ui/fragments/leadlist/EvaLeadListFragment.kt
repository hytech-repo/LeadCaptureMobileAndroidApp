package com.eva.lead.capture.ui.fragments.leadlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.databinding.FragmentEvaLeadListBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaLeadListFragment :
    BaseFragment<FragmentEvaLeadListBinding, EvaLeadListViewModel>(EvaLeadListViewModel::class.java) {
    private lateinit var mContext: Context

    private val leadListAdapter: EvaLeadListAdapter by lazy {
        EvaLeadListAdapter(mContext)
    }

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
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
        this.initView()
        this.initListener()
        this.fetchLeadList()
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Lead list"
        this.initRecyclerView()
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRecyclerView() {
        binding.rvLeadList.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = leadListAdapter
        }
    }

    private fun fetchLeadList() {
        lifecycleScope.launch {
            val leadList = viewModel.getLeadList().firstOrNull()
            if (leadList != null) {
                leadListAdapter.setLeadDataList(leadList)
            }
        }
    }


    companion object {
        fun newInstance() = EvaLeadListFragment()
    }
}