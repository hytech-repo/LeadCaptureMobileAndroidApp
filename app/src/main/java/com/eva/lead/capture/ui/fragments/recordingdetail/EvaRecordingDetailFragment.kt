package com.eva.lead.capture.ui.fragments.recordingdetail

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaRecordingDetailBinding
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.showToast
//import com.google.cloud.speech.v1.RecognitionAudio
//import com.google.cloud.speech.v1.RecognitionConfig
//import com.google.cloud.speech.v1.RecognizeRequest
//import com.google.cloud.speech.v1.SpeechClient
//import com.google.protobuf.ByteString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

class EvaRecordingDetailFragment : BaseFragment<FragmentEvaRecordingDetailBinding,EvaRecordingDetailViewModel>(
    EvaRecordingDetailViewModel::class.java) {
    private var mContext: Context? = null
    private lateinit var recordingId: String
    private var mediaPlayer: MediaPlayer? = null
    private var isPlayingAudio = false
    private var audioFile: File? = null
    private var speechRecognizer: SpeechRecognizer? = null

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
        this.initObserver()
        this.initListener()
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
    }

    private fun playAudio() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioFile?.absolutePath)  // Set the audio file path
                    prepare()  // Prepare the media player
                    setOnCompletionListener {
                        // Audio has finished playing, reset the UI
                        isPlayingAudio = false
                        updatePlayPauseButton()
                    }
                }
            }
            mediaPlayer?.start()
            isPlayingAudio = true
            updatePlayPauseButton()
        } catch (e: Exception) {
            e.printStackTrace()
            mContext!!.showToast("Error playing audio", ToastType.ERROR)
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlayingAudio = false
        updatePlayPauseButton()
    }

    private fun updatePlayPauseButton() {
        if (isPlayingAudio) {
            binding.ivPlayPause.setImageResource(R.drawable.ic_pause)
        } else {
            binding.ivPlayPause.setImageResource(R.drawable.ic_play)
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            try {
                val recordingDetail = viewModel.getRecordingDetail(recordingId).firstOrNull()
                if (recordingDetail != null) {
                    audioFile = File(recordingDetail.filePath ?: "")
                }
            } catch (e: Exception) {
                mContext?.showToast("Error fetching recording details", ToastType.ERROR)
            }
        }
    }

    private fun initBundle() {
        recordingId = arguments?.getString("recording_id" , "0").toString()
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


