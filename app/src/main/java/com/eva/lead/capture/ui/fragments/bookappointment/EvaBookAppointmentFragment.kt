package com.eva.lead.capture.ui.fragments.bookappointment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaBookAppointmentBinding
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EvaBookAppointmentFragment :
    BaseFragment<FragmentEvaBookAppointmentBinding, EvaBookAppointmentViewModel>(
        EvaBookAppointmentViewModel::class.java
    ) {
    private lateinit var mContext: Context
    private lateinit var lastGeneratedDate: LocalDate
    private var isLoading = false
    private val appointmentDateListAdapter : AppointmentDateListAdapter by lazy {
        AppointmentDateListAdapter(mContext)
    }
    private val timeSlotAdapter: AppointmentTimeSlotAdapter by lazy {
        AppointmentTimeSlotAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaRecordingDetailFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): FragmentEvaBookAppointmentBinding {
        return FragmentEvaBookAppointmentBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        lastGeneratedDate = LocalDate.now()
        this.initView()
        this.initListener()
        this.loadInitialDates()
    }


    private fun initView() {
        this.showDateRecyclerView()
        this.showTimeSlotList()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
    }

    private fun showDateRecyclerView() {
        appointmentDateListAdapter.onDateSelected = { selectedDate ->
            handleDateSelection(selectedDate)
        }
        binding.rvDatesList.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = appointmentDateListAdapter

            // Add the scroll listener for dynamic loading
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Logic to detect when the user is near the end of the list
                    val threshold = 5 // Load when 5 items remain
                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold) && firstVisibleItemPosition >= 0) {
                        loadMoreDates()
                    }
                }
            })
        }
    }

    private fun showTimeSlotList() {
        binding.rvTimeSlots.apply {
            layoutManager = GridLayoutManager(mContext, 3)
            adapter = timeSlotAdapter
        }
    }

    private fun loadInitialDates() {
        // Load the first chunk (e.g., 30 days)
        val initialList = generateNextDays(lastGeneratedDate, 30, true)
        appointmentDateListAdapter.setDateList(initialList)

        // Update the last generated date for the next chunk
        lastGeneratedDate = initialList.lastOrNull()?.let {
            LocalDate.parse(it.fullDate)
        } ?: LocalDate.now()
    }

    private fun initListener() {
        binding.rgMode.setOnCheckedChangeListener { btn, checkBtnId ->
            binding.llcLocation.visibility = if (checkBtnId == R.id.rbInPerson) View.VISIBLE else View.GONE
        }
    }

    private fun loadMoreDates() {
        if (isLoading) return
        isLoading = true

        // 💡 FIX: Access the current list using the new public getter
        val combinedList = appointmentDateListAdapter.currentList.toMutableList()

        // Generate the next chunk of dates (e.g., 15 more days)
        // We start one day after the last date we loaded
        val newDates = generateNextDays(lastGeneratedDate.plusDays(1), 15, false)

        // Add the new dates
        combinedList.addAll(newDates)

        // Re-set the full list in the adapter
        appointmentDateListAdapter.setDateList(combinedList)

        // Update the starting point for the next load
        lastGeneratedDate = newDates.lastOrNull()?.let {
            LocalDate.parse(it.fullDate)
        } ?: lastGeneratedDate.plusDays(15)

        Log.d("Scrolling", "Loaded ${newDates.size} new dates. Total: ${appointmentDateListAdapter.itemCount}")

        isLoading = false
    }

    private fun generateNextDays(
        startDate: LocalDate,
        count: Int,
        selectFirst: Boolean
    ): List<DateItem> {
        val dates = mutableListOf<DateItem>()
        var currentDate = startDate

        val numberFormatter = DateTimeFormatter.ofPattern("d")
        val dayNameFormatter = DateTimeFormatter.ofPattern("EEE")
        val fullDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (i in 0 until count) {
            val dateItem = DateItem(
                dayNumber = currentDate.format(numberFormatter),
                dayName = currentDate.format(dayNameFormatter),
                fullDate = currentDate.format(fullDateFormatter),
                isSelected = (selectFirst && i == 0)
            )
            dates.add(dateItem)
            currentDate = currentDate.plusDays(1)
        }
        return dates
    }

    private fun handleDateSelection(selectedDate: DateItem) {
        Log.d(
            "DateSelection",
            "New Date Selected: ${selectedDate.fullDate} (${selectedDate.dayName})"
        )
        Toast.makeText(
            requireContext(),
            "Fetching times for ${selectedDate.dayName}, ${selectedDate.dayNumber}",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun newInstance() = EvaBookAppointmentFragment()
    }

}