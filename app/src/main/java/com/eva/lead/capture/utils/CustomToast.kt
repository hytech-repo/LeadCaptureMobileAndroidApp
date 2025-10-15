package com.eva.lead.capture.utils

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.eva.lead.capture.R

object CustomToast {
    fun show(context: Context, message: String, type: ToastType, length: Int = Toast.LENGTH_SHORT) {
        val toast = Toast(context)
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        val container: RelativeLayout = layout.findViewById(R.id.toastRoot)
        val textView: TextView = layout.findViewById(R.id.toastMessage)
        val imageView: ImageView = layout.findViewById(R.id.toastIcon)

        textView.text = message

        when (type) {
            ToastType.SUCCESS -> {
                container.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.toast_success_bg
                    )
                )
                imageView.setImageResource(R.drawable.ic_circle_tick)
            }

            ToastType.ERROR -> {
                container.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.toast_error_bg
                    )
                )
                imageView.setImageResource(R.drawable.ic_cancel_circle)
            }

            ToastType.WARNING -> {
                container.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.toast_warning_bg
                    )
                )
                imageView.setImageResource(R.drawable.ic_warning)
            }

            ToastType.INFO -> {
                container.background.setTint(ContextCompat.getColor(context, R.color.toast_info_bg))
                imageView.setImageResource(R.drawable.ic_circle_info)
            }
        }

        toast.view = layout
        toast.setGravity(Gravity.END or Gravity.TOP, 50, 100)
        toast.duration = length // default ~2s, we’ll repeat manually
        toast.show()
    }
}