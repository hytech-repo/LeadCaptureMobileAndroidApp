package com.eva.lead.capture.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

class RoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    private val radius = 20f // corner radius in pixels

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans = mChart.getTransformer(dataSet.axisDependency)
        val drawBorder = dataSet.barBorderWidth > 0f

        val buffer = mBarBuffers[index]
//        buffer.setPhases(1f, 0.7f)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)

        for (j in buffer.buffer.indices step 4) {
            val left = buffer.buffer[j]
            val top = buffer.buffer[j + 1]
            val right = buffer.buffer[j + 2]
            val bottom = buffer.buffer[j + 3]

            // Set paint color based on entry color
            val entryColor = if (j / 4 < dataSet.colors.size) dataSet.colors[j / 4] else Color.BLACK
            mRenderPaint.color = entryColor

            // Draw only top corners rounded
            val path = Path()
            path.moveTo(left, bottom)
            path.lineTo(left, top + radius)
            path.quadTo(left, top, left + radius, top)
            path.lineTo(right - radius, top)
            path.quadTo(right, top, right, top + radius)
            path.lineTo(right, bottom)
            path.close()

            c.drawPath(path, mRenderPaint)

            if (drawBorder) {
                mBarBorderPaint.color = entryColor
                c.drawPath(path, mBarBorderPaint)
            }
        }
    }

    override fun drawHighlighted(c: Canvas, indices: Array<out Highlight>?) {
        if (indices == null) return

        val barData = mChart.barData ?: return

        for (high in indices) {
            val set = barData.getDataSetByIndex(high.dataSetIndex) as? IBarDataSet ?: continue
            if (!set.isHighlightEnabled) continue

            val e = set.getEntryForXValue(high.x, high.y) ?: continue
            val entryIndex = set.getEntryIndex(e)

            val buffer = mBarBuffers[high.dataSetIndex]

            // Each bar is 4 values in buffer: left, top, right, bottom
            val j = entryIndex * 4
            if (j + 3 >= buffer.buffer.size) continue

            val left = buffer.buffer[j]
            val top = buffer.buffer[j + 1]
            val right = buffer.buffer[j + 2]
            val bottom = buffer.buffer[j + 3]

            // Draw rounded highlight
            val radius = 20f
            val path = Path()
            path.moveTo(left, bottom)
            path.lineTo(left, top + radius)
            path.quadTo(left, top, left + radius, top)
            path.lineTo(right - radius, top)
            path.quadTo(right, top, right, top + radius)
            path.lineTo(right, bottom)
            path.close()

            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.alpha = set.highLightAlpha
            c.drawPath(path, mHighlightPaint)
        }
    }
}