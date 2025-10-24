package com.eva.lead.capture.ui.fragments.appointment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaAppointmentBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaAppointmentFragment :
    BaseFragment<FragmentEvaAppointmentBinding, EvaAppointmentViewModel>(EvaAppointmentViewModel::class.java) {
    private lateinit var mContext: Context

    private val appointmentAdapter: EvaAppointmentAdapter by lazy {
        EvaAppointmentAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaAppointmentFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAppointmentBinding {
        return FragmentEvaAppointmentBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
        this.initView()
        this.initListener()
        this.fetchAppointmentList()
    }

    private fun initView() {
        this.initToolbar()
        this.initRecyclerView()
    }

    private fun initToolbar() {
        binding.incToolbar.llcbtn.visibility = View.VISIBLE
        binding.incToolbar.tvTitle.text = mContext.getString(R.string.eva_appointment)
        binding.incToolbar.ivBtnImage.visibility = View.GONE
        binding.incToolbar.ivBack.visibility = View.GONE
        binding.incToolbar.ivUserImage.visibility = View.VISIBLE
        binding.incToolbar.tvBtnName.text = "Book Appointment"
    }

    private fun initRecyclerView() {
        binding.rvAppointmentList.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = appointmentAdapter
        }
    }

    private fun initListener() {
        binding.incToolbar.llcbtn.setOnClickListener {
            findNavController().navigate(R.id.action_evaAppointmentFragment_to_evaBookAppointmentFragment)
        }
        binding.incToolbar.ivUserImage.setOnClickListener {
            findNavController().navigate(R.id.action_evaAppointmentFragment_to_evaUserProfileFragment)
        }
    }

    private fun fetchAppointmentList() {
        lifecycleScope.launch {
            val appointmentList = viewModel.getAllApointments().firstOrNull()
            if (!appointmentList.isNullOrEmpty()) {
                appointmentAdapter.setAppointmentList(appointmentList)
            }
        }
    }

    companion object {
        fun newInstance() = EvaAppointmentFragment()
    }
}