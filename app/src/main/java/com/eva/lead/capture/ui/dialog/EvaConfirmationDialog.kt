package com.eva.lead.capture.ui.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.eva.lead.capture.databinding.ConfirmationDialogBinding

class EvaConfirmationDialog : AdaptiveDialogFragment<ConfirmationDialogBinding>() {
    private var tvHeading: String? = ""
    private var tvSubHeading: String? = ""
    private var tvPrimaryBtn: String? = ""
    private var tvSecondaryBtn: String? = ""
    private var ivIcon: Int = 0
    private var iconBgcolor: Int = 0
    private lateinit var mcontext: Context

    var onConfirmationListener: (isPrimaryBtnClicked: Boolean) -> Unit = { isPrimaryBtnClicked -> }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mcontext = context
    }


    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): ConfirmationDialogBinding {
        return ConfirmationDialogBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.initView()
        this.initListener()
    }

    private fun initBundle() {
        if (arguments != null) {
            ivIcon = arguments!!.getInt("ivIcon", 0)
            tvHeading = arguments!!.getString("heading", "")
            tvSubHeading = arguments!!.getString("sub_heading", "")
            tvPrimaryBtn = arguments!!.getString("primary_btn_text", "")
            tvSecondaryBtn = arguments!!.getString("seconday_btn_text", "")
            iconBgcolor = arguments!!.getInt("icon_bgcolor", 0)
        }
    }

    private fun initView() {
        binding.ivIcon.setImageResource(ivIcon)
        binding.tvHeading.text = tvHeading
        binding.tvSubHeading.text = tvSubHeading
        binding.tvPrimaryBtn.text = tvPrimaryBtn
        binding.tvSecondaryBtn.text = tvSecondaryBtn

        val color = ContextCompat.getColor(mcontext, iconBgcolor)
        val alphaBgColor = ColorUtils.setAlphaComponent(color, 15)
        binding.ivIcon.backgroundTintList = ColorStateList.valueOf(color)
        binding.vbg.backgroundTintList = ColorStateList.valueOf(alphaBgColor)
    }

    private fun initListener() {
        binding.tvPrimaryBtn.setOnClickListener {
            onConfirmationListener.invoke(true)
        }

        binding.tvSecondaryBtn.setOnClickListener {
            onConfirmationListener.invoke(false)
        }
    }
}