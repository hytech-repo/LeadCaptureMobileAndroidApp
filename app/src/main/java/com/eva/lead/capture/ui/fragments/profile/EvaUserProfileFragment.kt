package com.eva.lead.capture.ui.fragments.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.eva.lead.capture.ui.activities.MainActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.ui.dialog.EvaConfirmationDialog
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.changeDrawableBgAndStroke
import com.eva.lead.capture.utils.hasInternet
import com.eva.lead.capture.utils.showToast
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
//            ProfileOption("My Appointment", R.drawable.ic_add_calender),
            ProfileOption("Questions", R.drawable.ic_question),
            ProfileOption("Recording", R.drawable.ic_mic),
            ProfileOption("Need Help", R.drawable.ic_help),
//            ProfileOption("Sync Data", R.drawable.ic_sync),
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
        adapter.onItemClick = { option, position ->
            if (option.label == "Questions") {
                findNavController().navigate(R.id.action_evaUserProfileFragment_to_evaQuestionsFragment)
            } else if (option.label == "Need Help") {
                findNavController().navigate(R.id.action_evaUserProfileFragment_to_evaHelpFragment)
            } else if (option.label == "Device") {
                findNavController().navigate(R.id.action_evaUserProfileFragment_to_evaDeviceListFragment)
            } else if (option.label == "Recording") {
                findNavController().navigate(R.id.action_evaUserProfileFragment_to_evaRecordingListFragment)
            } else if (option.label == "Privacy Policy") {
                openPrivacyPolicyFragment()
            } else if (option.label == "Sign out") {
                showConfirmationDialog()
            }
        }
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

    private fun openPrivacyPolicyFragment() {
        if (mContext.hasInternet()) {
            val bundle = Bundle()
            bundle.putString("web_view_url", "https://www.evareg.com/privacy-policy/")
            bundle.putString("page_title", "Privacy Policy")
            findNavController().navigate(R.id.action_evaUserProfileFragment_to_evaWebviewPageFragment, bundle)
        } else {
            mContext.showToast(R.string.internet_not_available, ToastType.ERROR)
        }
    }

    private fun showConfirmationDialog() {
        val confirmationDialog = EvaConfirmationDialog()
        val bundle = Bundle()
        bundle.putString("heading", mContext.getString(R.string.sign_out_header))
        bundle.putString("sub_heading", mContext.getString(R.string.sign_out_subheading))
        bundle.putString("primary_btn_text", mContext.getString(R.string.cancel))
        bundle.putString("seconday_btn_text", mContext.getString(R.string.eva_sign_out))
        bundle.putInt("ivIcon", R.drawable.ic_signout_upward)
        bundle.putInt("icon_bgcolor", R.color.toast_error_bg)
        confirmationDialog.arguments = bundle
        confirmationDialog.apply {
            onConfirmationListener = { isPrimaryBtnClicked ->
                if (!isPrimaryBtnClicked) {
                    showProgressDialog(false)
                    clearLoginUserData()
                }
                dismiss()
            }
        }.show(requireActivity().supportFragmentManager, "EvaSignOutDialog")
    }

    private fun clearLoginUserData() {
        Handler(Looper.getMainLooper()).postDelayed({
            prefManager.remove(AppConstants.USER_ID)
            prefManager.remove(AppConstants.FIRST_NAME)
            prefManager.remove(AppConstants.LAST_NAME)
            prefManager.remove(AppConstants.LEAD_CODE)
            prefManager.remove(AppConstants.USER_EMAIL)
            prefManager.remove(AppConstants.REFRESH_TOKEN)
            hideProgressDialog()
            navigateToMainActivity()
        }, 3000)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(mContext, MainActivity::class.java)
        mContext.startActivity(intent)
        requireActivity().overridePendingTransition(0, 0)
    }

    private fun fetchExhibitorFromDb() {
        lifecycleScope.launch {
            val leadCode = prefManager.get(AppConstants.LEAD_CODE, "")
            val exhibitor = viewModel.checkExhibitor(leadCode).firstOrNull()
            exhibitor?.let { showDetailOnUI(it) }
            val leads = viewModel.getLeadList().firstOrNull()
            if (leads != null) {
                showLeadDetailOnUI(leads.size)
            }
        }
    }

    private fun showLeadDetailOnUI(size: Int) {
//        val colorRes = if (size != 0) R.color.color_lime_green else R.color.status_yellow
//        val color = ContextCompat.getColor(mContext, colorRes)
//        val bgColor = ColorUtils.setAlphaComponent(color, 15)
//        binding.incLeads.ivIcon.imageTintList = ColorStateList.valueOf(color)
//        binding.incLeads.ivIcon.backgroundTintList = ColorStateList.valueOf(bgColor)
//        binding.incLeads.tvCount.setTextColor(color)
        if (size != 0) {
            binding.incLeads.tvCount.text = "$size"
        } else {
            binding.incLeads.tvCount.text = "0"
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
