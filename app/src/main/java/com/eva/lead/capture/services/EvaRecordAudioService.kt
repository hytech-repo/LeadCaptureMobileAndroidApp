package com.eva.lead.capture.services

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.eva.lead.capture.data.local.AppDatabase
import com.eva.lead.capture.data.repository.AppDbRepositoryImpl
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.domain.repository.AppDbRepository
import com.eva.lead.capture.utils.AppLogger
import com.eva.lead.capture.utils.getExternalFolderPath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class EvaRecordAudioService : Service() {
    companion object {
        private const val TAG = "EvaRecordAudioService"
    }

    private val binder = AudioBinder()
    private var recorder: MediaRecorder? = null
    private var startTime = 0L
    private var job: Job? = null
    private var outputFile: File? = null
    private var isRecording = false
    private var duration: Long = 0L
    private var isPaused = false

    private lateinit var log: AppLogger
    private var progressListener: ((Int, Int) -> Unit)? = null
    private var repositoryDb: AppDbRepository? = null

    override fun onCreate() {
        super.onCreate()
        log = AppLogger(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log.d(TAG, "Service onStartCommand")

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun setOnProgressListener(progress: ((Int, Int) -> Unit)) {
        this.progressListener = progress
    }

    fun removeProgressCallback() {
        this.progressListener = null
    }

    fun startRecording() {
        if (isRecording) return

        val appDatabase = AppDatabase.getInstance(applicationContext)
        repositoryDb = AppDbRepositoryImpl(appDatabase)

        // Prepare output file path (internal storage)
        val dir = applicationContext.getExternalFolderPath("recording")
        outputFile = File(dir, "recording_${System.currentTimeMillis()}.m4a")

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this.applicationContext)
        } else {
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile!!.absolutePath)
            prepare()
            start()
        }

        startTime = System.currentTimeMillis()
        isRecording = true
        isPaused = false
        onRecordingProgress()
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isRecording && !isPaused) {
            recorder?.pause()
            isPaused = true
            isRecording = false
            stopProgressTracking()
            log.d(TAG, "Recording paused.")
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isPaused) {
            recorder?.resume()
            isPaused = false
            isRecording = true
            startProgressTracking()
            log.d(TAG, "Recording resumed.")
        }
    }

    // Update progress every second
    fun onRecordingProgress() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                duration = (System.currentTimeMillis() - startTime) / 1000
                val amplitude = recorder?.maxAmplitude ?: 0
                    progressListener?.invoke(duration.toInt(), amplitude)
                delay(50)
            }
        }
    }

    fun stopRecording(): File? {
        job?.cancel()
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        return outputFile // finalized file is ready now
    }

    private fun startProgressTracking() {
        stopProgressTracking()
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && isRecording) {
                duration = (System.currentTimeMillis() - startTime) / 1000
                val amplitude = recorder?.maxAmplitude ?: 0
                progressListener?.invoke(duration.toInt(), amplitude)
                delay(100)
            }
        }
    }

    private fun stopProgressTracking() {
        job?.cancel()
        job = null
    }

    fun saveRecordingIntoDb(audioFile: File) {
        CoroutineScope(Dispatchers.Default).launch {
            val recordingDate = System.currentTimeMillis()  // This will give you the current date and time as a Long

            // Calculate or get the duration of the recording (assuming you have the duration in milliseconds)
//            val recordingDuration = duration ?: 0L

            val recording = LeadAudioRecording(
                recordingName = audioFile.nameWithoutExtension,
                fileName = audioFile.name,
                filePath = audioFile.absolutePath,
                type = "recording",
                recordingDate = recordingDate,
                duration = duration
            )

            repositoryDb?.insertMediaFile(recording)
        }

    }

    fun isRecordingInProgress(): Boolean = isRecording
    fun isRecordingPaused(): Boolean = isPaused

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        recorder?.release()
        log.i(TAG, "EvaRecordAudioService destroyed")
    }

    inner class AudioBinder : Binder() {
        fun getService(): EvaRecordAudioService = this@EvaRecordAudioService
    }
}