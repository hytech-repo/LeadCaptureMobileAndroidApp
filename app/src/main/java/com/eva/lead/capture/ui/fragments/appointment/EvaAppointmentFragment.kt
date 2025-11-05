package com.eva.lead.capture.ui.fragments.appointment

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaAppointmentBinding
import com.eva.lead.capture.domain.model.entity.Appointment
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.TimeZone

class EvaAppointmentFragment :
    BaseFragment<FragmentEvaAppointmentBinding, EvaAppointmentViewModel>(EvaAppointmentViewModel::class.java) {
    private lateinit var mContext: Context
    private val calendarPermissions =
        arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)

    private val appointmentAdapter: EvaAppointmentAdapter by lazy {
        EvaAppointmentAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
        this.TAG = "EvaAppointmentFragment"
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAppointmentBinding {
        return FragmentEvaAppointmentBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        (requireActivity() as EventHostActivity).showHideBottomNavBar(true)
        this.initView()
        this.initListener()
        this.fetchAppointmentList()
    }

    private fun initView() {
        this.initToolbar()
        this.initRecyclerView()
    }

    private fun initToolbar() {
        binding.incToolbar.llcbtn.visibility = View.VISIBLE
        binding.incToolbar.tvTitle.text = mContext.getString(R.string.eva_appointment)
        binding.incToolbar.ivBtnImage.visibility = View.GONE
        binding.incToolbar.ivBack.visibility = View.GONE
        binding.incToolbar.ivUserImage.visibility = View.VISIBLE
        binding.incToolbar.tvBtnName.text = "Book Appointment"
    }

    private fun initRecyclerView() {
        appointmentAdapter.onItemClickListener = { model, view, position ->
            if (view.id == R.id.ivOptions) {
                showPopupMenu(model, view, position)
            } else {
                navigateToAppointmentDetail(model, position)
            }
        }
        binding.rvAppointmentList.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = appointmentAdapter
        }
    }

    private fun showPopupMenu(
        model: Appointment,
        view: View,
        position: Int
    ) {
        val popupMenu = PopupMenu(mContext, view)
        // Inflate menu from XML
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.appointment_popup, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_view -> {
                    navigateToAppointmentDetail(model, position)
                    true
                }

                R.id.menu_addcalendar -> {
                    checkCalendarPermission(model, position)
                    true
                }

                R.id.menu_reschedule -> {
                    rescheduleAppointment(model, position)
                    true
                }

                R.id.menu_cancel -> {
                    cancelAppointment(model, position)
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    private fun rescheduleAppointment(
        model: Appointment,
        position: Int
    ) {
        val bundle = Bundle()
        bundle.putParcelable("appointment_detail", model)
        bundle.putString("appointment_mode", "reschedule")
        findNavController().navigate(
            R.id.action_evaAppointmentFragment_to_evaBookAppointmentFragment,
            bundle
        )
    }

    private fun cancelAppointment(
        model: Appointment,
        position: Int
    ) {
        model.isDelete = true
        viewModel.updateAppointment(model) {
            fetchAppointmentList()
        }
    }

    private fun checkCalendarPermission(
        model: Appointment,
        position: Int
    ) {
        if (!hasPermission(calendarPermissions)) {
            requestPermission(calendarPermissions, 10023)
        } else {
            addAppointmentIntoCalendar(model)
        }
    }

    override fun onPermissionResult(permission: Map<String, Boolean>, requestCode: Int) {
        super.onPermissionResult(permission, requestCode)
        if (permission[Manifest.permission.WRITE_CALENDAR] == true) {

        }
    }

    private fun navigateToAppointmentDetail(
        model: Appointment,
        position: Int
    ) {
        val bundle = Bundle()
        bundle.putParcelable("appointment_detail", model)
        findNavController().navigate(
            R.id.action_evaAppointmentFragment_to_evaAppointmentDetailFragment,
            bundle
        )
    }

    private fun initListener() {
        binding.incToolbar.llcbtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("appointment_mode", "book")
            findNavController().navigate(R.id.action_evaAppointmentFragment_to_evaBookAppointmentFragment, bundle)
        }
        binding.incToolbar.ivUserImage.setOnClickListener {
            findNavController().navigate(R.id.action_evaAppointmentFragment_to_evaUserProfileFragment)
        }
    }

    private fun addAppointmentIntoCalendar(appointment: Appointment) {
        val beginTime = appointment.timestamp ?: 0L  // 1 hour later
        val endTime = beginTime + 3600000  // 1 hour duration

        val calendarUri: Uri = CalendarContract.Events.CONTENT_URI
        val eventValues = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, beginTime)
            put(CalendarContract.Events.DTEND, endTime)
            put(CalendarContract.Events.TITLE, appointment.userName)
            put(CalendarContract.Events.DESCRIPTION, appointment.subject)
            put(CalendarContract.Events.CALENDAR_ID, 1)  // Default calendar
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val eventUri: Uri? = mContext.contentResolver?.insert(calendarUri, eventValues)
        if (eventUri != null) {
            val eventId = eventUri.lastPathSegment?.toLongOrNull()
            eventId?.let {
                log.d("CalendarEvent", "Event added with ID: $it")
            }
        } else {
            log.e("CalendarEvent", "Failed to add event")
        }
    }

    private fun fetchAppointmentList() {
        lifecycleScope.launch {
            val appointmentList = viewModel.getAllApointments().firstOrNull()
            if (!appointmentList.isNullOrEmpty()) {
                binding.noAppointment.visibility = View.GONE
                appointmentAdapter.setAppointmentList(appointmentList)
            } else {
                binding.noAppointment.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        fun newInstance() = EvaAppointmentFragment()
    }
}