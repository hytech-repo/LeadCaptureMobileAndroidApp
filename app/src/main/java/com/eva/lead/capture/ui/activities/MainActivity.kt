package com.eva.lead.capture.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ActivityMainBinding
import com.eva.lead.capture.ui.base.BaseActivity
import com.eva.lead.capture.utils.AppLogger

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var log: AppLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log = AppLogger(this@MainActivity)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}