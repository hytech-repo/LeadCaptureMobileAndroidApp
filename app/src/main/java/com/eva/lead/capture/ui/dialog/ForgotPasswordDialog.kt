package com.eva.lead.capture.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.databinding.ForgotDialogBinding
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.showToast

class ForgotPasswordDialog : AdaptiveDialogFragment<ForgotDialogBinding>() {
    private var mContext: Context? = null
    private val emailRegex = Regex(AppConstants.EMAIL_REGEX)
    var onDialogConfirmationListener: (status: Boolean, email: String) -> Unit =
        { status, email -> }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ForgotDialogBinding {
        return ForgotDialogBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initListener()
    }

    private fun initListener() {
        binding.btnSent.setOnClickListener {
            onDialogConfirmationListener.invoke(true, "")
//            if (validateForgotField()) {
//                val email = binding.etEmailAddress.text.toString()
//                onDialogConfirmationListener.invoke(true, email)
//            }
        }

//        binding.tvCancel.setOnClickListener {
//            dismissNow()
//        }
    }

    private fun validateForgotField(): Boolean {
        val email = binding.etEmailAddress.text
//        if (email.isNullOrEmpty()) {
//            mContext?.showToast(R.string.empty_email_field, ToastType.ERROR)
//            return false
//        }
        if (!emailRegex.matches(email.toString())) {
            mContext?.showToast(R.string.invalid_email_msg, ToastType.ERROR)
            return false
        }
        return true
    }
}