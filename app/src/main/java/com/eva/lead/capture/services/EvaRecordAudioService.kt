package com.eva.lead.capture.services

import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.eva.lead.capture.utils.AppLogger
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

    private lateinit var log: AppLogger
    private var progressListener: ((Int, Int) -> Unit)? = null

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

        // Prepare output file path (internal storage)
        outputFile = File(filesDir, "recording_${System.currentTimeMillis()}.mp4")

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

        onRecordingProgress()
    }

    // Update progress every second
    fun onRecordingProgress() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val elapsed = (System.currentTimeMillis() - startTime) / 1000
                val amplitude = recorder?.maxAmplitude ?: 0
                    progressListener?.invoke(elapsed.toInt(), amplitude)
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

    fun isRecordingInProgress(): Boolean = isRecording

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