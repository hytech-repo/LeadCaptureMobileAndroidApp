package com.eva.lead.capture.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.data.local.AppDatabase
import com.eva.lead.capture.data.repository.AppDbRepositoryImpl
import com.eva.lead.capture.databinding.ActivityMainBinding
import com.eva.lead.capture.domain.model.entity.dummyUser
import com.eva.lead.capture.ui.base.BaseActivity
import com.eva.lead.capture.utils.AppLogger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var log: AppLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log = AppLogger(this@MainActivity)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        this.startWorking()
        this.handleNavigation()
    }

    private fun startWorking() {
        GlobalScope.launch {
            val appDb = AppDatabase.getInstance(this@MainActivity)
            val appDbRepository = AppDbRepositoryImpl(appDb)
            appDbRepository.insertExhibitor(dummyUser)
        }
    }

    private fun handleNavigation() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val leadCode = prefManager.get(AppConstants.LEAD_CODE, "")
        if (leadCode.isNotEmpty()) {
            val isLicenseActivate = prefManager.get(AppConstants.LICENSE_ACTIVATED, false)
            if (!isLicenseActivate) {
                navController.popBackStack(R.id.nav_graph, true)
                navController.navigate(R.id.eventActivationFragment)
            } else {
                navigateToEventHostActivity()
            }
        }
    }

    private fun navigateToEventHostActivity() {
        val intent = Intent(this@MainActivity, EventHostActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}