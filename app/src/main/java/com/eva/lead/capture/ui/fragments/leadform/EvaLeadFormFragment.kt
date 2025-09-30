package com.eva.lead.capture.ui.fragments.leadform

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.FragmentEvaLeadFormBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.services.EvaRecordAudioService
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.ui.fragments.camera.CapturedBusinessCardData
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.getDrawableStatus
import com.eva.lead.capture.utils.observe
import com.eva.lead.capture.utils.showToast

class EvaLeadFormFragment :
    BaseFragment<FragmentEvaLeadFormBinding, EvaLeadFormViewModel>(EvaLeadFormViewModel::class.java) {

    private lateinit var mContext: Context
    private var isRecording = false
    private var startTime: Long = 0
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable
    private var fileName: String = ""
    private lateinit var mediaRecorder: MediaRecorder
    private var recordService: EvaRecordAudioService? = null
    private var isBound = false
    private var userInfo: CapturedBusinessCardData? = null
    private val emailRegex = Regex(AppConstants.EMAIL_REGEX)
    private val phoneRegex = Regex(AppConstants.PHONE_REGEX)

    private val mediaAdapter: EvaAttachedMediaAdapter by lazy {
        EvaAttachedMediaAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaAddManuallyFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaLeadFormBinding {
        return FragmentEvaLeadFormBinding.inflate(inflater, container, false)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(mContext, EvaRecordAudioService::class.java)
//        mContext.startService(intent)
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.init()
        this.initObserver()
        this.initListener()
    }

    private fun initBundle() {
        if (arguments != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                userInfo = arguments!!.getParcelable("user_info", CapturedBusinessCardData::class.java)
            } else {
                userInfo = arguments!!.getParcelable<CapturedBusinessCardData>("user_info")
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as EvaRecordAudioService.AudioBinder
            recordService = binder.getService()
            isBound = true
            checkAudioPermission()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            recordService = null
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
    }

    private fun init() {
        binding.incToolbar.tvTitle.text = "Add Manually"
        binding.incToolbar.llcbtn.visibility = View.GONE
        binding.incToolbar.tvRecording.visibility = View.VISIBLE


        userInfo?.let { this.randerInfo(it) }

        this.initMediaRecyclerView()
    }

    private fun randerInfo(data: CapturedBusinessCardData) {
        if (data.businessCardInfo.isNotEmpty()) {
            val name = data.businessCardInfo["name"]?.split(" ")
            if (name != null && name.isNotEmpty()) {
                if (name.size == 2) {
                    binding.etFirstName.setText(name[0])
                    binding.etLastName.setText(name[1])
                } else {
                    binding.etFirstName.setText(name[0])
                }
            }
            val email = data.businessCardInfo["email"]
            if (!email.isNullOrEmpty()) {
                binding.etEmail.setText(email)
            }
            val phone = data.businessCardInfo["phone"]
            if (!phone.isNullOrEmpty()) {
                binding.etPhoneNumber.setText(phone)
            }
            val companyName = data.businessCardInfo["company"]
            if (!companyName.isNullOrEmpty()) {
                binding.etCompanyName.setText(companyName)
            }
        }
    }

    private fun initMediaRecyclerView() {
        binding.rvUploadMedia.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = mediaAdapter
        }
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if (validateLoginField()) {
                saveLeadData()
            }
        }

        binding.rgLeads.setOnCheckedChangeListener { _, checkedId ->
            resetRadioButtonBackground()
            when (checkedId) {
                R.id.hotLead -> {
                    binding.hotLead.background = mContext.getDrawableStatus("hot")
                }

                R.id.mediumLead -> {
                    binding.mediumLead.background = mContext.getDrawableStatus("medium")
                }

                R.id.coldLead -> {
                    binding.coldLead.background = mContext.getDrawableStatus("cold")
                }
            }
        }

//        binding.incToolbar.tvRecording.setOnClickListener {
//            if (!isRecording) {
//                // Start Recording
//                startRecording()
//            } else {
//                // Stop Recording
//                stopRecording()
//            }
//        }
    }

    private fun showProgressOfAudio() {
        recordService?.setOnProgressListener { progress, ampl ->
            val hrs = progress / 3600
            val mins = (progress % 3600) / 60
            val secs = progress % 60
            val duration = if (hrs > 0) {
                String.format("%02d:%02d:%02d", hrs, mins, secs)
            } else {
                String.format("%02d:%02d", mins, secs)
            }
            log.d("Recording", "progress: $progress, ampl $ampl")
            binding.incToolbar.tvRecording.text = duration
        }
        recordService?.startRecording()
    }

    override fun onStop() {
        super.onStop()
        recordService?.removeProgressCallback()
    }

    private fun resetRadioButtonBackground() {
        binding.hotLead.background = mContext.getDrawable(R.drawable.bg_rounded_stroke_16)
        binding.mediumLead.background = mContext.getDrawable(R.drawable.bg_rounded_stroke_16)
        binding.coldLead.background = mContext.getDrawable(R.drawable.bg_rounded_stroke_16)
    }

    private fun initObserver() {
        viewModel.apply {
            observe(loader) { showLoader() }
        }
    }

    private fun showLoader() {
        showProgressDialog(false)
    }

    private fun saveLeadData() {
        val tag = when (binding.rgLeads.checkedRadioButtonId) {
            R.id.hotLead -> "hot"
            R.id.mediumLead -> "medium"
            R.id.coldLead -> "cold"
            else -> ""
        }

        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhoneNumber.text.toString()
        val company = binding.etCompanyName.text.toString()
        val additional = binding.etAdditionalInfo.text.toString()
        val leadData = EvaLeadData(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            companyName = company,
            additionalInfo = additional,
            tag = tag,
            timestamp = System.currentTimeMillis()
        )

        viewModel.saveLeadData(leadData)
        findNavController().popBackStack()
    }

    private fun validateLoginField(): Boolean {
        if (binding.etFirstName.text.isNullOrEmpty()) {
            mContext.showToast("First Name is required", ToastType.ERROR)
            return false
        }
        if (binding.etLastName.text.isNullOrEmpty()) {
            mContext.showToast("Last Name is required", ToastType.ERROR)
            return false
        }
        val email = binding.etEmail.text
        if (email.isNullOrEmpty()) {
            mContext.showToast("Email is required", ToastType.ERROR)
            return false
        }
        if (!emailRegex.matches(email)) {
            mContext.showToast("Email is invalid", ToastType.ERROR)
            return false
        }
        val phone = binding.etPhoneNumber.text
        if (!phoneRegex.matches(phone)) {
            mContext.showToast("Phone number is invalid", ToastType.ERROR)
            return false
        }
        if (binding.rgLeads.checkedRadioButtonId == 0) {
            mContext.showToast("Please select tag", ToastType.ERROR)
            return false
        }
        return true
    }

    private fun checkAudioPermission() {
        if (checkPermissions()) {
            showProgressOfAudio()
        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            mContext,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onPermissionResult(permission: Map<String, Boolean>, requestCode: Int) {
        if (permission[Manifest.permission.RECORD_AUDIO] == true) {
            this.showProgressOfAudio()
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
    }
}
