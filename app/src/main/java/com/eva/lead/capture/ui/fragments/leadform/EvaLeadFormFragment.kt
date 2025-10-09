package com.eva.lead.capture.ui.fragments.leadform

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
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
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
        this.randerLeadDetail()
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
    }

    private fun showLeadDetailOnUI(leadDetail: EvaLeadData) {
        binding.btnSave.text = "Update Lead"
        binding.etFirstName.setText(leadDetail.firstName)
        binding.etLastName.setText(leadDetail.lastName)
        binding.etEmail.setText(leadDetail.email)
        binding.etPhoneNumber.setText(leadDetail.phone)
        binding.etNote.setText(leadDetail.notes)
        binding.etCompanyName.setText(leadDetail.companyName)
        binding.etAdditionalInfo.setText(leadDetail.additionalInfo)
        binding.incToolbar.tvRecording.visibility = View.GONE
        when (leadDetail.tag) {
            "hot" -> binding.hotLead.isChecked = true
            "warm" -> binding.mediumLead.isChecked = true
            "cold" -> binding.coldLead.isChecked = true
        }
        if (!leadDetail.imageFileNames.isNullOrEmpty()) {
            val imageDir = mContext.getExternalFolderPath("clicked_image")
            val imageFile = leadDetail.imageFileNames!!.split(",")
            for (image in imageFile) {
                val imageFile = File(imageDir, image)
                if (imageFile.exists()) {
                    selectedFile.add(imageFile.absolutePath)
                }
            }
            mediaAdapter.setList(selectedFile)
        }
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
                    binding.mediumLead.background = mContext.getDrawableStatus("warm")
                }

                R.id.coldLead -> {
                    binding.coldLead.background = mContext.getDrawableStatus("cold")
                }
            }
        }
    }

    private fun randerLeadDetail() {
        if (leadDetail != null) {
            showLeadDetailOnUI(leadDetail!!)
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

    private fun displayQuickNote(questions: List<QuestionInfo>) {
        val typeRegular = ResourcesCompat.getFont(mContext, R.font.sf_pro_regular)
        val typeMedium = ResourcesCompat.getFont(mContext, R.font.sf_pro_medium)
        questions.forEach { questionInfo ->

            val questionBlock = LinearLayoutCompat(mContext).apply {
                orientation = LinearLayoutCompat.VERTICAL
                setPadding(16, 8, 16, 16)
            }

            // Create a TextView for the question
            val questionTextView = TextView(mContext).apply {
                text = questionInfo.question
                textSize = 16f
                setTypeface(typeMedium, Typeface.BOLD)
                setPadding(0, 16, 0, 16)
            }

            // Add TextView for the question to the container
            questionBlock.addView(questionTextView)

            // Create a RadioGroup for the options
            if (questionInfo.isMultipleChoice == false) {
                val radioGroup = RadioGroup(mContext).apply {
                    orientation = RadioGroup.VERTICAL
                    setPadding(0, 16, 0, 16)
                }

                // Add RadioButton for each option in the options list
                questionInfo.options?.forEach { option ->
                    val radioButton = RadioButton(mContext).apply {
                        text = option
                        textSize = 14f
                        setTypeface(typeRegular, Typeface.NORMAL)
                        buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setPadding(0, 20, 0, 20)
                    }
                    radioGroup.addView(radioButton)
                }
                questionBlock.addView(radioGroup)
            } else {
                val radioGroup = LinearLayoutCompat(mContext).apply {
                    orientation = LinearLayoutCompat.VERTICAL
                    setPadding(0, 16, 0, 16)
                }
                questionInfo.options?.forEach { option ->
                    val checkBox = CheckBox(mContext).apply {
                        text = option
                        textSize = 14f
                        setTypeface(typeRegular, Typeface.NORMAL)
                        buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setPadding(0, 16, 0, 16)
                    }
                    radioGroup.addView(checkBox)
                }
                questionBlock.addView(radioGroup)
            }
            binding.llcQuickNote.addView(questionBlock)
        }
    }

    private fun displayQuestions(questions: List<QuestionInfo>) {
        val typeRegular = ResourcesCompat.getFont(mContext, R.font.sf_pro_regular)
        val typeMedium = ResourcesCompat.getFont(mContext, R.font.sf_pro_medium)
        questions.forEach { questionInfo ->
            // Create a TextView for the question

            val questionBlock = LinearLayoutCompat(mContext).apply {
                orientation = LinearLayoutCompat.VERTICAL
                setPadding(16, 8, 16, 16)
            }

            // Add question text to the question block
            val questionTextView = TextView(mContext).apply {
                text = questionInfo.question
                textSize = 16f
                setTypeface(typeMedium, Typeface.BOLD)
                setPadding(0, 16, 0, 16)
                setTextColor(ContextCompat.getColor(mContext, R.color.heading_text_color))
            }
            questionBlock.addView(questionTextView)

            // Showing options
            if (questionInfo.isMultipleChoice == false) {
                val radioGroup = RadioGroup(mContext).apply {
                    orientation = RadioGroup.VERTICAL
                    setPadding(0, 16, 0, 16)
                }

                // Add RadioButton for each option in the options list
                questionInfo.options?.forEach { option ->
                    val radioButton = RadioButton(mContext).apply {
                        text = option
                        textSize = 14f
                        buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setTypeface(typeRegular, Typeface.NORMAL)
                        setPadding(0, 20, 0, 20)
                    }
                    radioGroup.addView(radioButton)
                }
                questionBlock.addView(radioGroup)
            } else {
                val radioGroup = LinearLayoutCompat(mContext).apply {
                    orientation = LinearLayoutCompat.VERTICAL
                    setPadding(0, 16, 0, 16)
                }
                questionInfo.options?.forEach { option ->
                    val checkBox = CheckBox(mContext).apply {
                        text = option
                        textSize = 14f
                        setTypeface(typeRegular, Typeface.NORMAL)
                        buttonTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setTextColor(
                            ContextCompat.getColor(
                                mContext,
                                R.color.subheading_text_color
                            )
                        )
                        setPadding(0, 20, 0, 20)
                    }
                    radioGroup.addView(checkBox)
                }
                questionBlock.addView(radioGroup)
            }
            binding.llcQuestionContainer.addView(questionBlock)
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
        if (leadDetail == null) {
            recordService?.startRecording()
        }
    }

    override fun onStop() {
        super.onStop()
        recordService?.removeProgressCallback()
    }

    private fun resetRadioButtonBackground() {
        val bgDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_rounded_stroke_16)
        binding.hotLead.background = bgDrawable
        binding.mediumLead.background = bgDrawable
        binding.coldLead.background = bgDrawable
    }

    private fun initObserver() {
        viewModel.apply {
            observe(loader) { showLoader() }
        }
        lifecycleScope.launch {
            val questionList = viewModel.fetchQuestions("remote").firstOrNull()
            val localQuestionList = viewModel.fetchQuestions("question").firstOrNull()
            val quickNote = viewModel.fetchQuestions("note").firstOrNull()
            if (!quickNote.isNullOrEmpty()) {
                binding.llcQuickNote.visibility = View.VISIBLE
                displayQuickNote(quickNote)
            } else {
                binding.llcQuickNote.visibility = View.GONE
            }
            if (!questionList.isNullOrEmpty()) {
                displayQuestions(questionList)
            }
            if (!localQuestionList.isNullOrEmpty()) {
                displayQuestions(localQuestionList)
            }
        }
    }

    private fun showLoader() {
        showProgressDialog(false)
    }

    private fun saveLeadData() {
        val tag = when (binding.rgLeads.checkedRadioButtonId) {
            R.id.hotLead -> "hot"
            R.id.mediumLead -> "warm"
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
        val audioFileName = if (leadDetail == null) {
            recordService?.stopRecording()?.name
        } else {
            leadDetail!!.audioFilePath
        }
        val audioFile = recordService?.stopRecording()
        val quickNoteAnswers = getSelectedAnswers(binding.llcQuickNote)
        val questionAnswers = getSelectedAnswers(binding.llcQuestionContainer)
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
            audioFilePath = audioFileName,
            timestamp = System.currentTimeMillis(),
            quickNote = quickNoteAnswers,
            questionAnswer = questionAnswers
        )
        if (leadDetail == null) {
            audioFile?.let {
                recordService?.saveRecordingIntoDb(it)
            }
            viewModel.saveLeadData(leadData)
        } else {
            leadData.id = leadDetail!!.id
            viewModel.updateLeadData(leadData)
        }
        findNavController().popBackStack()
    }

    private fun getSelectedAnswers(container: LinearLayoutCompat): String {
        val sb = StringBuilder()

        for (i in 0 until container.childCount) {
            val questionBlock = container.getChildAt(i) as? LinearLayoutCompat ?: continue
            val questionText = (questionBlock.getChildAt(0) as? TextView)?.text.toString()
            val selectedOptions = mutableListOf<String>()

            for (j in 1 until questionBlock.childCount) {
                val optionsContainer = questionBlock.getChildAt(j)
                when (optionsContainer) {
                    is RadioGroup -> {
                        val selectedId = optionsContainer.checkedRadioButtonId
                        if (selectedId != -1) {
                            val radioButton = optionsContainer.findViewById<RadioButton>(selectedId)
                            selectedOptions.add(radioButton.text.toString())
                        }
                    }
                    is LinearLayoutCompat -> { // checkboxes
                        for (k in 0 until optionsContainer.childCount) {
                            val checkBox = optionsContainer.getChildAt(k) as? CheckBox
                            if (checkBox?.isChecked == true) selectedOptions.add(checkBox.text.toString())
                        }
                    }
                }
            }

            if (selectedOptions.isNotEmpty()) {
                if (sb.isNotEmpty()) sb.append("::") // separator between questions
                sb.append("$questionText=${selectedOptions.joinToString(",")}")
            }
        }

        return sb.toString()
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
