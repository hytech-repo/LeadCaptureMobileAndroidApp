package com.eva.lead.capture.ui.fragments.recordingdetail

//import com.google.cloud.speech.v1.RecognitionAudio
//import com.google.cloud.speech.v1.RecognitionConfig
//import com.google.cloud.speech.v1.RecognizeRequest
//import com.google.cloud.speech.v1.SpeechClient
//import com.google.protobuf.ByteString
import android.content.Context
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaRecordingDetailBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.getExternalFolderPath
import com.eva.lead.capture.utils.showToast
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

class EvaRecordingDetailFragment :
    BaseFragment<FragmentEvaRecordingDetailBinding, EvaRecordingDetailViewModel>(
        EvaRecordingDetailViewModel::class.java
    ) {
    private lateinit var mContext: Context
    private lateinit var recordingName: String
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingAudio = false
    private var audioFile: File? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var visualizer: Visualizer? = null
    private var leadList: ArrayList<EvaLeadData> = arrayListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaRecordingDetailFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaRecordingDetailBinding {
        return FragmentEvaRecordingDetailBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.initView()
        this.initListener()
        this.fetchLeadListDb()
        this.prepareMediaPlayer()
    }

    private fun initBundle() {
        if (arguments != null) {
            recordingName = arguments!!.getString("recording_name", "")
        }
    }

    private fun initAdapter() {
        val leadsName = leadList.map { "${it.firstName} ${it.lastName}" }
        val leadAdapter = ArrayAdapter<String>(
            mContext,
            R.layout.dropdown_text_item,
            leadsName.toMutableList()
        )
        binding.actvLeadDropDown.apply {
            setOnItemClickListener { _, _, position, _ ->
                onItemSelected(leadList[position])
            }
            setAdapter(leadAdapter)
        }
    }

    private fun onItemSelected(data: EvaLeadData) {
        data.audioFilePath = recordingName
        viewModel.updateLeadData(data)
    }

    private fun fetchLeadListDb() {
        lifecycleScope.launch {
            val leads = viewModel.getLeadList().firstOrNull()
            if (leads != null) {
                leadList = leads as ArrayList
                initAdapter()
            }
        }
    }

    private fun prepareMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioFile?.absolutePath)  // Set the audio file path
            prepare()  // Prepare the media player
            setOnCompletionListener {
                // Audio has finished playing, reset the UI
                isPlayingAudio = false
                updatePlayPauseButton()
            }
        }
        setupVisualizer()
    }

    private fun setupVisualizer() {
        mediaPlayer?.audioSessionId?.let { sessionId ->
            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1] // max capture size
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer?,
                        waveform: ByteArray?,
                        samplingRate: Int
                    ) {
                        waveform?.forEach { b ->
                            binding.waveRecording.addAmplitude(b.toInt() * 75) // scale for view
                        }
                    }

                    override fun onFftDataCapture(
                        visualizer: Visualizer?,
                        fft: ByteArray?,
                        samplingRate: Int
                    ) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false)

                enabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        visualizer?.release()
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivPlayPause.setOnClickListener {
            if (isPlayingAudio) {
                pauseAudio()
            } else {
                playAudio()
                transcribeAudio()
            }
        }

    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Recordings"
        binding.incToolbar.llcbtn.visibility = View.GONE

        val audioDir = mContext.getExternalFolderPath("recording")
        audioFile = File(audioDir, recordingName)
    }

    private fun playAudio() {
        try {
            mediaPlayer?.start()
            isPlayingAudio = true
            updatePlayPauseButton()
            visualizer?.enabled = true
        } catch (e: Exception) {
            e.printStackTrace()
            mContext!!.showToast("Error playing audio", ToastType.ERROR)
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlayingAudio = false
        updatePlayPauseButton()
        visualizer?.enabled = false
        binding.waveRecording.clearWaveform() // Clear old bars
    }

    private fun updatePlayPauseButton() {
        if (isPlayingAudio) {
            binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.ivPlayPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun transcribeAudio() {
//        lifecycleScope.launch {
//            try {
//                val speechClient = SpeechClient.create()
//
//                val audioBytes = Files.readAllBytes(audioFile!! as Path?) // Read the audio file into a byte array
//                val audio = RecognitionAudio.newBuilder()
//                    .setContent(ByteString.copyFrom(audioBytes))
//                    .build()
//
//                val config = RecognitionConfig.newBuilder()
//                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16) // Set encoding type (change as needed)
//                    .setSampleRateHertz(16000) // Set sample rate (adjust if needed)
//                    .setLanguageCode("en-US") // Set language
//                    .build()
//
//                val request = RecognizeRequest.newBuilder()
//                    .setConfig(config)
//                    .setAudio(audio)
//                    .build()
//
//                val response = speechClient.recognize(request)
//
//                // Extract the transcribed text
//                val transcription = response.resultsList.joinToString("\n") { it.alternativesList[0].transcript }
//
//                // Display the transcribed text in the UI
//                binding.tvAudioTranscript.text = transcription
//
//                speechClient.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                mContext?.showToast("Error during transcription", ToastType.ERROR)
//            }
//
//        }
    }

    companion object {
        fun newInstance() = EvaRecordingDetailFragment()
    }

}


