package com.eva.lead.capture.ui.fragments.leadlist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.databinding.FragmentEvaLeadListBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaLeadListFragment :
    BaseFragment<FragmentEvaLeadListBinding, EvaLeadListViewModel>(EvaLeadListViewModel::class.java) {
    private lateinit var mContext: Context
    private var tags: MutableList<String> = mutableListOf()
    private var leadList: List<EvaLeadData>? = mutableListOf()

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
        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (!leadList.isNullOrEmpty()) {
                val filterList = leadList!!.filter {
                    it.firstName?.lowercase()?.contains(text?.toString()?.lowercase() ?: "") == true
                            || it.lastName?.lowercase()
                        ?.contains(text?.toString()?.lowercase() ?: "") == true
                            || it.companyName?.lowercase()
                        ?.contains(text?.toString()?.lowercase() ?: "") == true
                            || it.designation?.lowercase()
                        ?.contains(text?.toString()?.lowercase() ?: "") == true
                            || it.email?.lowercase()
                        ?.contains(text?.toString()?.lowercase() ?: "") == true

                }
                leadListAdapter.setLeadDataList(filterList)
            }

        }
        binding.cbAll.setOnCheckedChangeListener { btn, isChecked ->
            if (isChecked) {
                enableOtherCheckBox(!isChecked)
                tags = mutableListOf("all")
            } else {
                tags.remove("all")
            }
            filterListAccordingToTags()
        }
        binding.cbHotLead.setOnClickListener {
            if (binding.cbHotLead.isChecked) {
                binding.cbAll.isChecked = false
                tags.add("hot")
            } else {
                tags.remove("hot")
            }
            filterListAccordingToTags()
        }
        binding.cbMediumLead.setOnClickListener {
            if (binding.cbMediumLead.isChecked) {
                binding.cbAll.isChecked = false
                tags.add("medium")
            } else {
                tags.remove("medium")
            }
            filterListAccordingToTags()
        }
        binding.cbColdLead.setOnClickListener {
            if (binding.cbColdLead.isChecked) {
                binding.cbAll.isChecked = false
                tags.add("cold")
            } else {
                tags.remove("cold")
            }
            filterListAccordingToTags()
        }
    }

    private fun filterListAccordingToTags() {
        if (!leadList.isNullOrEmpty()) {
            val filterList = if (tags.isNotEmpty()) {
                if (tags.size == 3) {
                    leadList!!.filter { it.tag == tags[0] || it.tag == tags[1] || it.tag == tags[2] }
                } else if (tags.size == 2) {
                    leadList!!.filter { it.tag == tags[0] || it.tag == tags[1] }
                } else {
                    if (tags[0] != "all") {
                        leadList!!.filter { it.tag == tags[0] }
                    } else {
                        leadList
                    }
                }
            } else {
                leadList
            }
            leadListAdapter.setLeadDataList(filterList ?: emptyList())
            updateCountOnUI(filterList)
        }

    }

    private fun enableOtherCheckBox(isEnable: Boolean) {
//        binding.cbHotLead.isEnabled = isEnable
        binding.cbHotLead.isChecked = false
//        binding.cbMediumLead.isEnabled = isEnable
        binding.cbMediumLead.isChecked = false
//        binding.cbColdLead.isEnabled = isEnable
        binding.cbColdLead.isChecked = false
    }

    private fun initRecyclerView() {
        binding.rvLeadList.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = leadListAdapter
        }
    }

    private fun fetchLeadList() {
        lifecycleScope.launch {
            leadList = viewModel.getLeadList().firstOrNull()
            if (leadList != null) {
                leadListAdapter.setLeadDataList(leadList!!)
            }
            updateCountOnUI(leadList)
        }
    }

    private fun updateCountOnUI(leadList: List<EvaLeadData>?) {
        val count = leadList?.size ?: 0
        binding.tvLeadCounts.text = "$count Leads"
    }


    companion object {
        fun newInstance() = EvaLeadListFragment()
    }
}