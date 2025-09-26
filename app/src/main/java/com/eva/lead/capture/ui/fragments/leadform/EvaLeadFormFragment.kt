package com.eva.lead.capture.ui.fragments.leadform

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaLeadFormBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.getDrawableStatus
import com.eva.lead.capture.utils.observe
import com.eva.lead.capture.utils.showToast
import java.io.IOException

class EvaLeadFormFragment :
    BaseFragment<FragmentEvaLeadFormBinding, EvaLeadFormViewModel>(EvaLeadFormViewModel::class.java) {

    private lateinit var mContext: Context
    private var isRecording = false
    private var startTime: Long = 0
    private lateinit var handler: Handler
    private lateinit var updateTimeRunnable: Runnable
    private var fileName: String = ""
    private lateinit var mediaRecorder: MediaRecorder

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

    override fun startWorking(savedInstanceState: Bundle?) {
        this.init()
        this.initObserver()
        this.initListener()
    }

    private fun init() {
        binding.incToolbar.tvTitle.text = "Add Manually"
        binding.incToolbar.llcbtn.visibility = View.GONE
        binding.incToolbar.tvRecording.visibility = View.VISIBLE

        this.initMediaRecyclerView()


        handler = Handler(Looper.getMainLooper())

        // File path for the audio file
        fileName = context?.filesDir?.absolutePath + "/recording_${System.currentTimeMillis()}.3gp"

        // Initialize the MediaRecorder
        mediaRecorder = MediaRecorder()
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

        binding.incToolbar.tvRecording.setOnClickListener {
            if (!isRecording) {
                // Start Recording
                startRecording()
            } else {
                // Stop Recording
                stopRecording()
            }
        }
    }

    private fun startRecording() {
        if (checkPermissions()) {
            try {
                mediaRecorder.apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(fileName)

                    prepare()
                    start()

                    // Update UI to show recording time
                    startTime = System.currentTimeMillis()
                    isRecording = true

                    // Start updating the text view with elapsed time
                    startRecordingTimeUpdate()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            requestPermissions()
        }
    }

    override fun onStop() {
        super.onStop()
        this.stopRecording()
    }

    private fun startRecordingTimeUpdate() {
        updateTimeRunnable = Runnable {
            val elapsedMillis = System.currentTimeMillis() - startTime
            val elapsedSeconds = elapsedMillis / 1000
            val formattedTime = formatElapsedTime(elapsedSeconds)
            binding.incToolbar.tvRecording.text = "Recording: $formattedTime"

            // Repeat this runnable every 100ms
            handler.postDelayed(updateTimeRunnable, 100)
        }

        // Start the timer immediately
        handler.post(updateTimeRunnable)
    }

    // Format elapsed time to display as 0:05 (minutes:seconds)
    private fun formatElapsedTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    private fun stopRecording() {
        if (isRecording) {
            mediaRecorder.apply {
                stop()
                reset()
            }
            isRecording = false

            // Stop the timer
            handler.removeCallbacks(updateTimeRunnable)
            binding.incToolbar.tvRecording.text = "Recording Stopped"
        }
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
        if (binding.etEmail.text.isNullOrEmpty()) {
            mContext.showToast("Email is required", ToastType.ERROR)
            return false
        }
        if (binding.rgLeads.checkedRadioButtonId == 0) {
            mContext.showToast("Please select tag", ToastType.ERROR)
            return false
        }
        return true
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

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1001
    }
}
