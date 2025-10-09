package com.eva.lead.capture.ui.fragments.deviceform

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.FragmentEvaDeviceDetailBinding
import com.eva.lead.capture.domain.model.entity.DeviceInfo
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.showToast
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaDeviceDetailFragment :
    BaseFragment<FragmentEvaDeviceDetailBinding, EvaDeviceDetailViewModel>(EvaDeviceDetailViewModel::class.java) {

    private lateinit var mContext: Context
    private var exhibitor: Exhibitor? = null

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
        lifecycleScope.launch {
            val leadCode = prefManager.get(AppConstants.LEAD_CODE, "")
            exhibitor = viewModel.checkExhibitor(leadCode).firstOrNull()
            if (exhibitor != null) {
                showExhibitorDetailOnUI(exhibitor!!)
            }
        }
        binding.etDeviceName.setText("${Build.MANUFACTURER} ${Build.MODEL}")
    }

    private fun showExhibitorDetailOnUI(exhibitor: Exhibitor) {
        binding.etFirstName.isEnabled = exhibitor.firstName.isNullOrEmpty()
        binding.etLastName.isEnabled = exhibitor.lastName.isNullOrEmpty()
        binding.etEmail.isEnabled = exhibitor.email.isNullOrEmpty()
        binding.etFirstName.setText(exhibitor.firstName)
        binding.etLastName.setText(exhibitor.lastName)
        binding.etEmail.setText(exhibitor.email)
    }

    private fun initListener() {
        binding.saveButton.setOnClickListener {
            if (validateUI()) {
                navigateToOtherScreen()
            }
        }
    }

    private fun validateUI(): Boolean {
        val deviceName = binding.etDeviceName.text
        if (deviceName.isNullOrEmpty()) {
            mContext.showToast("Device name is empty", ToastType.ERROR)
            return false
        }
        return true
    }

    private fun navigateToOtherScreen() {
        exhibitor?.deviceName = binding.etDeviceName.text.toString()
        viewModel.updateExhibitor(exhibitor) {
            val intent = Intent(mContext, EventHostActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            mContext.startActivity(intent)
            requireActivity().overridePendingTransition(0, 0)
        }
    }

    companion object {
        fun newInstance() = EvaDeviceDetailFragment()
    }
}