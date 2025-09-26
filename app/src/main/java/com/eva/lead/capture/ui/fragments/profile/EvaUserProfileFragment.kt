package com.eva.lead.capture.ui.fragments.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.FragmentEvaUserProfileBinding
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.changeDrawableBgAndStroke
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EvaUserProfileFragment :
    BaseFragment<FragmentEvaUserProfileBinding, EvaUserProfileViewModel>(EvaUserProfileViewModel::class.java) {

    private lateinit var mContext: Context
    private var exhibitorData: Exhibitor? = null

    private val adapter: ProfileOptionsAdapter by lazy {
        ProfileOptionsAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        TAG = "EvaUserProfileFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaUserProfileBinding {
        return FragmentEvaUserProfileBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
        setupToolbar()
        setupRecyclerView()
        setupInfoCard()
        setupListeners()
        fetchExhibitorFromDb()
    }

    private fun setupToolbar() {
        binding.incToolbar.tvTitle.text = "Profile"
        binding.incToolbar.llcbtn.visibility = View.GONE
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewOptions.layoutManager = LinearLayoutManager(mContext)
        binding.recyclerViewOptions.adapter = adapter

        val options = listOf(
            ProfileOption("Device", R.drawable.ic_phone),
            ProfileOption("My Appointment", R.drawable.ic_add_calender),
            ProfileOption("Questions", R.drawable.ic_question),
            ProfileOption("Recording", R.drawable.ic_mic),
            ProfileOption("Need Help", R.drawable.ic_help),
            ProfileOption("Sync Data", R.drawable.ic_sync),
            ProfileOption("Privacy Policy", R.drawable.ic_privacy_policy),
            ProfileOption("Sign out", R.drawable.ic_sign_out)
        )

        adapter.updateProfileOption(options)
    }

    private fun setupInfoCard() {
        binding.incLeads.apply {
            tvType.text = "Leads"
            tvCount.setTextColor(ContextCompat.getColor(mContext, R.color.toast_success_bg))
            llcLeads.background = mContext.changeDrawableBgAndStroke(
                R.drawable.bg_rounded_status,
                R.color.toast_success_bg,
                2,
                15
            )
        }

        binding.incAppointments.apply {
            tvType.text = "Appointments"
            tvCount.setTextColor(ContextCompat.getColor(mContext, R.color.color_purple))
            ivIcon.setImageResource(R.drawable.ic_add_calender)
            llcLeads.background = mContext.changeDrawableBgAndStroke(
                R.drawable.bg_rounded_status,
                R.color.color_purple,
                2,
                15
            )
        }
    }

    private fun setupListeners() {
        binding.incUserInfo.tvEdit.setOnClickListener {
            exhibitorData?.let { exhibitor ->
                val bundle = Bundle().apply {
                    putString("lead_code", exhibitor.leadCode)
                    putString("first_name", exhibitor.firstName)
                    putString("last_name", exhibitor.lastName)
                    putString("email", exhibitor.email)
                    putString("device", exhibitor.deviceName)
                    putString("user_id", exhibitor.userId)
                }
                findNavController().navigate(
                    R.id.action_evaUserProfileFragment_to_evaEditProfileFragment,
                    bundle
                )
            }
        }

        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun fetchExhibitorFromDb() {
        lifecycleScope.launch {
            val leadCode = prefManager.get(AppConstants.LEAD_CODE, "")
            val exhibitor = viewModel.checkExhibitor(leadCode).firstOrNull()
            exhibitor?.let { showDetailOnUI(it) }
        }
    }

    private fun showDetailOnUI(exhibitor: Exhibitor) {
        exhibitorData = exhibitor

        binding.incUserInfo.apply {
            val initials = exhibitor.firstName?.firstOrNull()?.uppercaseChar()?.toString() ?: ""
            tvProfile.text = initials
            tvUserName.text = "${exhibitor.firstName.orEmpty()} ${exhibitor.lastName.orEmpty()}"
            tvEmail.text = exhibitor.email
            tvDeviceName.text = exhibitor.deviceName.orEmpty()
        }
    }

    companion object {
        fun newInstance() = EvaUserProfileFragment()
    }
}
