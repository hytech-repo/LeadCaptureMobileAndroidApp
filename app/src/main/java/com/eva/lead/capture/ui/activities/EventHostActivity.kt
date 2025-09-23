package com.eva.lead.capture.ui.activities

import android.os.Bundle
import com.eva.lead.capture.databinding.ActivityEventHostBinding
import com.eva.lead.capture.ui.base.BaseActivity
import com.eva.lead.capture.utils.AppLogger

class EventHostActivity : BaseActivity() {

    private lateinit var binding: ActivityEventHostBinding
    private lateinit var log: AppLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log = AppLogger(this@EventHostActivity)
        binding = ActivityEventHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}