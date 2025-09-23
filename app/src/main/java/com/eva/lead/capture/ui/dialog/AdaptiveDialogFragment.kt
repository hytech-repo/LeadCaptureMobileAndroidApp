package com.eva.lead.capture.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.eva.lead.capture.R
import com.eva.lead.capture.ui.base.BaseDialogFragment
import com.eva.lead.capture.utils.isTablet
import com.google.android.material.bottomsheet.BottomSheetDialog

/*
* AdaptiveDialogFragment is a base dialog fragment class
* that adapts it appearance depending on the device type.
*
* Features:
* - On **tablets**, it shows a regular Dialog with all corners rounded.
* - On **mobile phones**, it shows a BottomSheetDialog with only the top corners rounded.
*
* This class is generic with a ViewBinding type parameter [VB], so subclasses
* can provide their layout binding easily.
*
* */

abstract class AdaptiveDialogFragment<VB : ViewBinding> : BaseDialogFragment<VB>() {

    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    /**
     * Creates a Dialog instance depending on the device type:
     * - Tablet: uses a standard Dialog with all corners rounded.
     * - Mobile: uses a BottomSheetDialog with top corners rounded.
     *
     * @param savedInstanceState The saved instance state bundle.
     * @return The appropriate Dialog instance.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return if (mContext.isTablet()) {
//            super.onCreateDialog(savedInstanceState).apply {
//                window?.setBackgroundDrawableResource(R.drawable.bg_white_rounded_corners)
//            }
//        } else {
//            BottomSheetDialog(mContext, theme)/*.apply {
//                window?.setBackgroundDrawableResource(R.drawable.bg_rounded_top_corners)
//            }*/
//        }
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawableResource(R.drawable.bg_white_rounded_corners)
        }
    }

}