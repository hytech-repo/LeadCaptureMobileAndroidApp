package com.eva.lead.capture.ui.fragments.editprofile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.databinding.FragmentEvaEditProfileBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.showToast

class EvaEditProfileFragment() :
    BaseFragment<FragmentEvaEditProfileBinding, EvaEditProfileViewModel>(EvaEditProfileViewModel::class.java) {
    private var mContext: Context? = null
    private var leadCode: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var device: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaEditProfileFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaEditProfileBinding {
        return FragmentEvaEditProfileBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
        this.initBundle()
        this.initView()
        this.observer()
        this.initListener()
    }

    private fun observer() {
        viewModel.saveSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                mContext!!.showToast("Data saved successfully", ToastType.SUCCESS)
                findNavController().popBackStack()
            } else {
                mContext!!.showToast("Failed to save data", ToastType.ERROR)
            }
        }
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Edit Profile"
        binding.incToolbar.llcbtn.visibility = View.GONE
        setData()
    }

    private fun setData() {
        binding.etFirstName.setText(firstName)
        binding.etLastName.setText(lastName)
        binding.etEmail.setText(email)
        binding.etDevice.setText(device)
    }

    private fun initBundle() {
        leadCode = arguments?.getString("lead_code") ?: ""
        firstName = arguments?.getString("first_name") ?: ""
        lastName = arguments?.getString("last_name") ?: ""
        email = arguments?.getString("email")
        device = arguments?.getString("device")
    }

    private fun initListener() {
        binding.btnSave.setOnClickListener {
            saveData()
        }
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveData() {
        firstName = binding.etFirstName.text.toString()
        lastName = binding.etLastName.text.toString()
        email = binding.etEmail.text.toString()
        device = binding.etDevice.text.toString()

        if (firstName!!.isEmpty() || lastName!!.isEmpty() || email!!.isEmpty()) {
            mContext?.showToast("Please fill all required fields", ToastType.ERROR)
            return
        }

        viewModel.saveUserData(
            firstName.toString(),
            lastName.toString(),
            email.toString(),
            device.toString(),
            leadCode.toString()
        )
    }

    companion object {
        fun newInstance() = EvaEditProfileFragment()
    }
}