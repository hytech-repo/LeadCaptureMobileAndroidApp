package com.eva.lead.capture.ui.activities

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ActivityEventHostBinding
import com.eva.lead.capture.ui.base.BaseActivity
import com.eva.lead.capture.utils.AppLogger

class EventHostActivity : BaseActivity() {

    private lateinit var binding: ActivityEventHostBinding
    private lateinit var log: AppLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventHostBinding.inflate(layoutInflater)
        log = AppLogger(this@EventHostActivity)
        setContentView(binding.root)

        this.initView()
    }

    private fun initView() {
        val navController = findNavController(R.id.event_fragment_container)
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }

                R.id.nav_add_lead -> {
                    navController.navigate(R.id.evaAddLeadFragment)
                    true
                }

                R.id.nav_appointment -> {
                    navController.navigate(R.id.evaAppointmentFragment)
                    true
                }

                R.id.nav_total_lead -> {
                    navController.navigate(R.id.evaLeadListFragment)
                    true
                }

                else -> false
            }
        }
    }

    fun showHideBottomNavBar(isShown: Boolean) {
        binding.cvBottomNavBar.visibility = if (isShown) View.VISIBLE else View.GONE
    }

    fun activeNavMenu(menuId: Int) {
        binding.bottomNav.menu.findItem(menuId).isChecked = true
    }


}