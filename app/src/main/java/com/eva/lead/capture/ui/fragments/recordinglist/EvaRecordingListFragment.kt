package com.eva.lead.capture.ui.fragments.recordinglist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaRecordingListBinding
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaRecordingListFragment :
    BaseFragment<FragmentEvaRecordingListBinding, EvaRecordingListViewModel>(EvaRecordingListViewModel::class.java) {
    private var mContext: Context? = null
    private var tags: MutableList<String> = mutableListOf()
    private var recordingList: List<LeadAudioRecording>? = mutableListOf()

    private val recordingListAdapter: RecordingListAdapter by lazy {
        RecordingListAdapter(mContext!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaRecordingListFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaRecordingListBinding {
        return FragmentEvaRecordingListBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
        this.initListener()
        this.fetchRecordingList()
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Recordings"
        binding.incToolbar.llcbtn.visibility = View.GONE
        this.initRecyclerView()
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.etSearch.doOnTextChanged { text, start, before, count ->
            if (!recordingList.isNullOrEmpty()) {
                val filterList = recordingList!!.filter {
                    it.recordingName?.lowercase()?.contains(text?.toString()?.lowercase() ?: "") == true
                }
                showLeadListOnUI(filterList)
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

        recordingListAdapter.onItemClick = { option, position ->
//            val bundle = Bundle()
//            bundle.putString("recording_name", option.fileName.toString())
//            findNavController().navigate(R.id.action_evaRecordingListFragment_to_evaRecordingDetailFragment, bundle)
        }
    }

    private fun enableOtherCheckBox(isEnable: Boolean) {
//        binding.cbLead.isEnabled = isEnable
        binding.cbLead.isChecked = false
//        binding.cbPersonal.isEnabled = isEnable
        binding.cbPersonal.isChecked = false
    }

    private fun filterListAccordingToTags() {
        if (!recordingList.isNullOrEmpty()) {
            val filterList = if (tags.isNotEmpty()) {
                if (tags.size == 2) {
                    recordingList!!.filter { it.type == tags[0] || it.type == tags[1]  }
                } else {
                    if (tags[0] != "all") {
                        recordingList!!.filter { it.type == tags[0] }
                    } else {
                        recordingList
                    }
                }
            } else {
                recordingList
            }
            showLeadListOnUI(filterList)
        }

    }

    private fun showLeadListOnUI(record: List<LeadAudioRecording>?) {
        binding.rvRecordingList.visibility = if (record.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.tvNoRecordings.visibility = if (record.isNullOrEmpty()) View.VISIBLE else View.GONE
        recordingListAdapter.setRecordingList(record)
        binding.tvLeadCounts.text = "${record?.size?: 0} Recordings"
    }

    private fun initRecyclerView() {
        binding.rvRecordingList.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = recordingListAdapter
        }
    }

    private fun fetchRecordingList() {
        lifecycleScope.launch {
            recordingList = viewModel.getRecordingList().firstOrNull()
            if (recordingList != null) {
                recordingListAdapter.setRecordingList(recordingList!!)
            }
            showLeadListOnUI(recordingList)
        }
    }


    companion object {
        fun newInstance() = EvaRecordingListFragment()
    }

}