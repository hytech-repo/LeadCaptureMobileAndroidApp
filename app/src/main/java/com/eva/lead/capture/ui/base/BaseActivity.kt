package com.eva.lead.capture.ui.base

import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.eva.lead.capture.data.local.PrefManagerImpl
import com.eva.lead.capture.domain.repository.PrefManager
import com.eva.lead.capture.utils.ConnectivityObserver

abstract class BaseActivity : AppCompatActivity() {

    private var requestCode: Int = 0

    protected val prefManager: PrefManager by lazy {
        PrefManagerImpl(this@BaseActivity)
    }

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private val connectivityManager: ConnectivityManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                ConnectivityObserver.updateConnectionStatus(true)
            }

            override fun onLost(network: Network) {
                ConnectivityObserver.updateConnectionStatus(false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun onPause() {
        super.onPause()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            onPermissionResult(permissions, requestCode)
        }

    protected fun hasPermission(permissions: Array<String>): Boolean {
        for (element in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this@BaseActivity,
                    element
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    protected fun requestPermission(permission: Array<String>, requestCode: Int) {
        this.requestCode = requestCode
        requestPermissionLauncher.launch(permission)
    }

    protected open fun onPermissionResult(permission: Map<String, Boolean>, requestCode: Int) {

    }

}