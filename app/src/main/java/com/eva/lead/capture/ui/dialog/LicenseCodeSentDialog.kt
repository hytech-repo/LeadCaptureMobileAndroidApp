package com.eva.lead.capture.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentLicenseCodeSentDialogBinding

class LicenseCodeSentDialog : AdaptiveDialogFragment<FragmentLicenseCodeSentDialogBinding>() {
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentLicenseCodeSentDialogBinding {
        return FragmentLicenseCodeSentDialogBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {

    }
}