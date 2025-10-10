package com.eva.lead.capture.ui.fragments.leadlist

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaLeadListBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.FileUtils
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.getExternalFolderPath
import com.eva.lead.capture.utils.showToast
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter

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
        this.initView()
        this.initListener()
        this.fetchLeadList()
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Lead list"
        binding.incToolbar.ivBack.visibility = View.GONE
        binding.incToolbar.llcbtn.visibility = View.VISIBLE
        this.initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).activeNavMenu(R.id.nav_total_lead)
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
    }

    private fun initListener() {
        leadListAdapter.onItemClickListener = { data, position ->
            navigateToNextScreen(data, position)
        }
        binding.incToolbar.llcbtn.setOnClickListener {
            exportLeadsToMail()
        }
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
                tags.add("warm")
            } else {
                tags.remove("warm")
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

    private fun exportLeadsToMail() {
        val uris = ArrayList<Uri>()

        val csvFile = FileUtils.createZipFileOfLeads(mContext, leadList)
        if (csvFile != null) {
            val uri = FileProvider.getUriForFile(
                mContext,
                "${mContext.applicationContext.packageName}.fileprovider",
                csvFile
            )
            uris.add(uri)
        }

        val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "audio/*" // MIME type for audio files
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
//            putExtra(Intent.EXTRA_EMAIL, arrayOf("recipient@example.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Captured Leads")
            putExtra(Intent.EXTRA_TEXT, "Hi \n\n Please find the attached details of leads")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            mContext.startActivity(Intent.createChooser(intent, "Send email using:"))
        } catch (e: ActivityNotFoundException) {
            mContext.showToast("No email app found", ToastType.ERROR)
        }
    }

    private fun navigateToNextScreen(
        data: EvaLeadData,
        position: Int
    ) {
        val bundle = Bundle()
        bundle.putParcelable("lead_detail", data)
        findNavController().navigate(R.id.action_evaLeadListFragment_to_evaAddManualLead, bundle)
    }

    private fun showLeadListOnUI(record: List<EvaLeadData>?) {
        binding.rvLeadList.visibility = if (record.isNullOrEmpty()) View.GONE else View.VISIBLE
        binding.tvNoLeads.visibility = if (record.isNullOrEmpty()) View.VISIBLE else View.GONE
        leadListAdapter.setLeadDataList(record)
        binding.tvLeadCounts.text = "${record?.size?: 0} Leads"
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
            showLeadListOnUI(filterList)
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
            showLeadListOnUI(leadList)
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