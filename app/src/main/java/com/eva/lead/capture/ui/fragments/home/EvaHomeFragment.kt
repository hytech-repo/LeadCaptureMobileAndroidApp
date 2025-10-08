package com.eva.lead.capture.ui.fragments.home

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColor
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaHomeBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.RoundedBarChartRenderer
import com.eva.lead.capture.utils.toColor
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.HashSet
import java.util.Locale

class EvaHomeFragment :
    BaseFragment<FragmentEvaHomeBinding, EvaHomeViewModel>(EvaHomeViewModel::class.java) {
    private lateinit var mContext: Context
    private var leadList: List<EvaLeadData>? = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaHomeFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaHomeBinding {
        return FragmentEvaHomeBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initView()
        this.initListener()
        this.fetchLeadInfo()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
    }

    private fun fetchLeadInfo() {
        lifecycleScope.launch {
            leadList = viewModel.getLeadList().firstOrNull()
            if (leadList != null) {
                showLeadInfoDetailOnUi(leadList!!)
                showChartInfo()
            }
        }
    }

    private fun showChartInfo() {
        if (!leadList.isNullOrEmpty()) {
            this.showLineChart(leadList!!)
            this.showBarChart(leadList!!)
        }
    }

    private fun showLineChart(leadList: List<EvaLeadData>) {
        val sortedLeads = leadList.filter { it.timestamp != null }.sortedBy { it.timestamp }
        binding.tvLineLeadCount.text = "${leadList.size}"

//        val hourFormatter = SimpleDateFormat("yyyy-MM-dd HH:00", Locale.getDefault())
        val hourLabelFormatter = SimpleDateFormat("HH:00", Locale.getDefault())

        val calendar = Calendar.getInstance()
        val groupedByHour = sortedLeads.groupBy { lead ->
            calendar.timeInMillis = lead.timestamp!!
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }

//        val groupedByHour = sortedLeads.groupBy {
//            hourFormatter.format(Date(it.timestamp!!))
//        }

//        val firstTimestamp = hourFormatter.parse(
//            hourFormatter.format(Date(sortedLeads.first().timestamp!!))
//        )?.time?.toFloat() ?: 0f

//        val entries = groupedByHour.entries.map { (dateHourStr, leadsAtHour) ->
//            val hourTimeMillis = hourFormatter.parse(dateHourStr)?.time?.toFloat() ?: 0f
//            Entry(hourTimeMillis - firstTimestamp, leadsAtHour.size.toFloat()) // normalize
//        }.sortedBy { it.x }

        val entries = groupedByHour.entries.map { (hourTimestamp, leadsAtHour) ->
            Entry(hourTimestamp.toFloat(), leadsAtHour.size.toFloat())
        }.sortedBy { it.x }

//        val sortedHours = groupedByHour.keys.sorted()
//        for ((i, hour) in sortedHours.withIndex()) {
//            val count = groupedByHour[hour]?.size?.toFloat() ?: 0f
//            if (i == 0) {
//                // Add zero at start
//                entries.add(Entry(hour.toFloat(), 0f))
//            }
//            entries.add(Entry(hour.toFloat(), count))
//        }

        val dataSet = LineDataSet(entries, "Leads by Hour").apply {
            color = R.color.color_bluish_purple.toColor(mContext)
            setCircleColor(R.color.color_bluish_purple.toColor(mContext))
            circleRadius = 3f
            lineWidth = 2f
            valueTextSize = 10f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = R.color.color_purple_light.toColor(mContext)
            fillAlpha = 50
        }

        val lineData = LineData(dataSet)
        binding.lineChart.apply {
            data = lineData
            description.text = "Time in hour"
            axisRight.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisLeft.setDrawGridLines(false)
            xAxis.setDrawGridLines(false)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.labelRotationAngle = -45f
            xAxis.granularity = 60 * 60 * 1000f
            axisRight.setDrawGridLines(false)
            legend.isEnabled = true

            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return hourLabelFormatter.format(Date(value.toLong()))
                }
            }
            invalidate()
        }
    }

    private fun showBarChart(leadList: List<EvaLeadData>) {
        val tagCounts = leadList.groupingBy { it.tag ?: "Unknown" }.eachCount()
        val barEntries = ArrayList<BarEntry>()
        val tags = arrayOf("hot", "warm", "cold", "appointment")
        binding.tvBarLeadCount.text = "${leadList.size}"

        tags.forEachIndexed { index, tag ->
            val count = tagCounts[tag]?.toFloat() ?: 0f
            barEntries.add(BarEntry(index.toFloat(), count))
        }

        val barDataSet = BarDataSet(barEntries, "Leads by Tag")
        barDataSet.colors = listOf(
            R.color.toast_error_bg.toColor(mContext),   // hot
            R.color.status_yellow.toColor(mContext),  // warm
            R.color.status_blue.toColor(mContext),  // cold
            R.color.color_pinkish_red.toColor(mContext)  // appointment
        )

        val barData = BarData(barDataSet)
        barData.barWidth = 0.7f
        binding.barChart.apply {
            renderer = RoundedBarChartRenderer(this, animator, viewPortHandler)
            data = barData
            description.text = ""
            xAxis.valueFormatter =
                IndexAxisValueFormatter(tags)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            setVisibleXRangeMaximum(10f)
            isScaleXEnabled = false
            axisRight.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            invalidate() // refresh
        }
    }

    private fun showLeadInfoDetailOnUi(leadInfo: List<EvaLeadData>) {
        val colorRes =
            if (leadInfo.isNotEmpty()) R.color.color_lime_green else R.color.status_yellow
        val color = ContextCompat.getColor(mContext, colorRes)
        val bgColor = ColorUtils.setAlphaComponent(color, 15)
        binding.ivGroupIcon.imageTintList = ColorStateList.valueOf(color)
        binding.ivGroupIcon.backgroundTintList = ColorStateList.valueOf(bgColor)
        binding.tvLeadCount.setTextColor(color)
        if (leadInfo.isNotEmpty()) {
            binding.tvLeadCount.text = "${leadInfo.size}"
            binding.tvNoLead.text = mContext.getString(R.string.doing_good_msg)
        } else {
            binding.tvNoLead.text = mContext.getString(R.string.no_lead_yet)
        }
    }

    private fun initView() {
        binding.incToolbar.tvTitle.text = "Home"
    }

    private fun initListener() {
        binding.cvTotalLead.setOnClickListener {
            if (!leadList.isNullOrEmpty()) {
                findNavController().navigate(R.id.action_homeFragment_to_evaLeadListFragment)
            }
        }
        binding.incToolbar.ivUserImage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_evaUserProfileFragment)
        }
    }
}