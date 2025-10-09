package com.eva.lead.capture.ui.fragments.devicelist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.databinding.FragmentEvaDeviceListBinding
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaDeviceListFragment :
    BaseFragment<FragmentEvaDeviceListBinding, EvaDeviceListViewModel>(EvaDeviceListViewModel::class.java) {
    private var mContext: Context? = null
    private val deviceListAdapter: DeviceListAdapter by lazy {
        DeviceListAdapter(mContext!!)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaDeviceListFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaDeviceListBinding {
        return FragmentEvaDeviceListBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
        this.initListener()
        this.fetchDeviceList()
    }

    private fun fetchDeviceList() {
        lifecycleScope.launch {
            val deviceList = viewModel.getDeviceList().firstOrNull()
            if (deviceList != null) {
                deviceListAdapter.setDeviceList(deviceList)
            }
        }
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Device"
        binding.incToolbar.llcbtn.visibility = View.GONE
        this.initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rvDeviceList.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = deviceListAdapter
        }
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    companion object {
        fun newInstance() = EvaDeviceListFragment()
    }

}