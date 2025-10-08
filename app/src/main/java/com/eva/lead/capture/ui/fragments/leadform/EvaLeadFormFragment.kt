package com.eva.lead.capture.ui.fragments.leadform

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Typeface
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.FragmentEvaLeadFormBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.services.EvaRecordAudioService
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.ui.dialog.EvaConfirmationDialog
import com.eva.lead.capture.ui.fragments.camera.CapturedBusinessCardData
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.formatDuration
import com.eva.lead.capture.utils.getDrawableStatus
import com.eva.lead.capture.utils.getExternalFolderPath
import com.eva.lead.capture.utils.observe
import com.eva.lead.capture.utils.showToast
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

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
    private var leadDetail: EvaLeadData? = null
    private val emailRegex = Regex(AppConstants.EMAIL_REGEX)
    private val phoneRegex = Regex(AppConstants.PHONE_REGEX)
    private val audioPermission = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var selectedFile = arrayListOf<String>()
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted = false

    private val mediaAdapter: EvaAttachedMediaAdapter by lazy {
        EvaAttachedMediaAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaAddManuallyFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener("scan_result", this) { _, bundle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                userInfo = bundle.getParcelable("user_info", CapturedBusinessCardData::class.java)
            } else {
                userInfo = bundle.getParcelable<CapturedBusinessCardData>("user_info")
            }

            userInfo?.let { randerInfo(it) }
            userInfo?.imagePath?.let {
                selectedFile.add(it)
                mediaAdapter.setList(selectedFile)
            }
        }
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
        if (recordService != null) {
            checkAudioPermission()
        }
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.init()
        this.initObserver()
        this.initListener()
    }

    private fun initBundle() {
        if (arguments != null) {
            this.checkUserBundle()
            this.checkLeadDetail()
        }
    }

    private fun checkUserBundle() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            userInfo = arguments!!.getParcelable("user_info", CapturedBusinessCardData::class.java)
        } else {
            userInfo = arguments!!.getParcelable<CapturedBusinessCardData>("user_info")
        }
        if (userInfo != null) {
            selectedFile = arrayListOf()
            val image = userInfo!!.imagePath
            selectedFile.add(image)
        }
    }

    private fun checkLeadDetail() {
        leadDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments!!.getParcelable("lead_detail", EvaLeadData::class.java)
        } else {
            arguments!!.getParcelable("lead_detail")
        }
        if (leadDetail != null) {
            showLeadDetailOnUI(leadDetail!!)
        }
    }

    private fun showLeadDetailOnUI(leadDetail: EvaLeadData) {
        binding.etFirstName.setText(leadDetail.firstName)
        binding.etLastName.setText(leadDetail.lastName)
        binding.etEmail.setText(leadDetail.email)
        binding.etPhoneNumber.setText(leadDetail.phone)
        binding.etNote.setText(leadDetail.notes)
        binding.etCompanyName.setText(leadDetail.companyName)
        binding.etAdditionalInfo.setText(leadDetail.additionalInfo)
        if (leadDetail.audioFilePath != null) {
            binding.incAudio.audioPlayerContainer.visibility = View.VISIBLE
            loadAudioPlayer(leadDetail.audioFilePath!!)
        }
    }

    private fun loadAudioPlayer(audioFile: String) {
        mediaPlayer?.release()
        val dir = mContext.getExternalFolderPath("recording")
        val file = File(dir, audioFile)
        mediaPlayer = MediaPlayer.create(mContext, Uri.fromFile(file))
        binding.incAudio.seekBar.max = mediaPlayer?.duration ?: 0
        val total = mediaPlayer?.duration?.toLong()?.formatDuration() ?: 0
        binding.incAudio.tvTime.text = "00:00 / $total"
        mediaPlayer?.setOnCompletionListener {
            binding.incAudio.btnPlayPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun playAudio() {
        mediaPlayer?.start()
        binding.incAudio.btnPlayPause.setImageResource(R.drawable.ic_pause)
        updateSeekBar()
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        binding.incAudio.btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    private fun toggleMute() {
        if (isMuted) {
            // Unmute
            mediaPlayer?.setVolume(1f, 1f)
            binding.incAudio.btnVolume.setImageResource(R.drawable.ic_speaker)
        } else {
            // Mute
            mediaPlayer?.setVolume(0f, 0f)
            binding.incAudio.btnVolume.setImageResource(R.drawable.ic_speaker_off)
        }
        isMuted = !isMuted
    }

    private fun updateSeekBar() {
        handler = Handler(Looper.getMainLooper())
        mediaPlayer?.let {
            binding.incAudio.seekBar.progress = it.currentPosition
            val current = it.currentPosition.toLong().formatDuration()
            val total = it.duration.toLong().formatDuration()
            binding.incAudio.tvTime.text = "$current / $total"

            if (mediaPlayer?.isPlaying == true) handler.postDelayed({ updateSeekBar() }, 500)
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
            mediaAdapter.setList(selectedFile)
        }
    }

    private fun initMediaRecyclerView() {
        binding.rvUploadMedia.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = mediaAdapter
        }
    }

    private fun initListener() {
        mediaAdapter.onItemClickListener = { path, action, position ->
            if (action == "add") {
                openCameraForClickedImage()
            } else if (action == "remove") {
                selectedFile.remove(path)
                mediaAdapter.setList(selectedFile)
            } else {

            }
        }
        binding.incAudio.btnPlayPause.setOnClickListener {
            playPauseAudioFile()
        }
        binding.incAudio.btnVolume.setOnClickListener {
            toggleMute()
        }
        binding.incAudio.seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSave.setOnClickListener {
            if (validateLoginField()) {
                takeConfirmationFromUser()
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
    }

    private fun playPauseAudioFile() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                mediaPlayer?.pause()
                binding.incAudio.btnPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer?.start()
                binding.incAudio.btnPlayPause.setImageResource(R.drawable.ic_pause)
                updateSeekBar()
            }
        }
    }

    private fun displayQuestions(questions: List<QuestionInfo>) {
        questions.forEach { questionInfo ->
            // Create a TextView for the question
            val questionTextView = TextView(mContext).apply {
                text = questionInfo.question
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setPadding(16, 16, 16, 16)
            }

            // Add TextView for the question to the container
            binding.llcQuestionContainer.addView(questionTextView)

            // Create a RadioGroup for the options
            val radioGroup = RadioGroup(mContext).apply {
                orientation = RadioGroup.VERTICAL
                setPadding(16, 8, 16, 16)
            }

            // Add RadioButton for each option in the options list
            questionInfo.options?.forEach { option ->
                val radioButton = RadioButton(mContext).apply {
                    text = option
                    textSize = 14f
                    setPadding(16, 8, 16, 8)


                }
                radioGroup.addView(radioButton)
            }

            // Add the RadioGroup to the container
            binding.llcQuestionContainer.addView(radioGroup)
        }
    }

    private fun takeConfirmationFromUser() {
        val confirmationDialog = EvaConfirmationDialog()
        val bundle = Bundle()
        bundle.putString("heading", mContext.getString(R.string.save_lead))
        bundle.putString("sub_heading", mContext.getString(R.string.save_confirmation_lead))
        bundle.putString("primary_btn_text", mContext.getString(R.string.save_lead))
        bundle.putString("seconday_btn_text", mContext.getString(R.string.eva_discard))
        bundle.putInt("icon_bgcolor", R.color.color_lime_green)
        bundle.putInt("ivIcon", R.drawable.ic_user_grp)
        confirmationDialog.arguments = bundle
        confirmationDialog.isCancelable = false
        confirmationDialog.apply {
            onConfirmationListener = { isPrimaryBtnClicked ->
                if (isPrimaryBtnClicked) {
                    saveLeadData()
                }
                dismiss()
            }
        }.show(requireActivity().supportFragmentManager, "EvaConfirmationAudioDialog")
    }

    private fun openCameraForClickedImage() {
        val bundle = Bundle()
        bundle.putBoolean("send_fragment_result", true)
        findNavController().navigate(R.id.action_evaAddManualLead_to_evaCameraFragment, bundle)
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
        lifecycleScope.launch {
            val questionList = viewModel.fetchQuestions("remote").firstOrNull()
            if (!questionList.isNullOrEmpty()) {
                displayQuestions(questionList)
            }
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

        var namesCommaSeparated: String = ""
        if (selectedFile.isNotEmpty()) {
            val fileNames = selectedFile.mapNotNull { filePath ->
                val file = File(filePath)
                if (file.exists()) file.name else null
            }
            namesCommaSeparated = fileNames.joinToString(", ")
            log.d("Files", namesCommaSeparated)
        }

        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhoneNumber.text.toString()
        val company = binding.etCompanyName.text.toString()
        val additional = binding.etAdditionalInfo.text.toString()
        val notes = binding.etNote.text.toString()
        val audioFile = recordService?.stopRecording()
        val leadData = EvaLeadData(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            companyName = company,
            additionalInfo = additional,
            notes = notes,
            imageFileNames = namesCommaSeparated,
            tag = tag,
            audioFilePath = audioFile?.name,
            timestamp = System.currentTimeMillis()
        )
        audioFile?.let {
            recordService?.saveRecordingIntoDb(it)
        }

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
        if (hasPermission(audioPermission)) {
            showProgressOfAudio()
        } else {
            requestPermission(audioPermission, 10023)
        }
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
