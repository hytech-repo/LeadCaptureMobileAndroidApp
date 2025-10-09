package com.eva.lead.capture.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.eva.lead.capture.R

class WaveformView @JvmOverloads constructor(
    val mContext: Context, attrs: AttributeSet? = null
) : View(mContext, attrs) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(mContext, R.color.primary_color)
        isAntiAlias = true
    }

    private val amplitudes = mutableListOf<Int>()
    private val barWidth = 10f
    private val barSpacing = 5f

    fun addAmplitude(amplitude: Int) {
        amplitudes.add(amplitude)
        // Keep the bars within the view width
        val maxBars = (width / (barWidth + barSpacing)).toInt()
        if (amplitudes.size > maxBars) amplitudes.removeAt(0)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerY = height / 2f
        var x = width.toFloat()

        amplitudes.asReversed().forEach { amp ->
            // Normalize amplitude to view height
            val barHeight = (amp / 32767f) * centerY
            // Draw vertical bar (from center up and down)
            canvas.drawRect(
                x - barWidth,
                centerY - barHeight,
                x,
                centerY + barHeight,
                paint
            )
            x -= barWidth + barSpacing
        }
    }

    fun clearWaveform() {
        amplitudes.clear()
        invalidate()
    }
}