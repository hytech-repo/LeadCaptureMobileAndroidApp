package com.eva.lead.capture.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.eva.lead.capture.utils.AppLogger

class AppBackService : Service() {

    companion object {
        private const val TAG = "AppBackService"
    }

    private val binder = LocalBinder()
    private lateinit var log: AppLogger

    override fun onCreate() {
        super.onCreate()
        log = AppLogger(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        log.i(TAG, "AppBackService destroyed")
    }

    inner class LocalBinder : Binder() {
        fun getService(): AppBackService = this@AppBackService
    }
}