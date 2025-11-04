package com.eva.lead.capture.ui.fragments.bookappointment

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaBookAppointmentBinding
import com.eva.lead.capture.domain.model.entity.Appointment
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.services.EvaRecordAudioService
import com.eva.lead.capture.ui.activities.EventHostActivity
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.ToastType
import com.eva.lead.capture.utils.showToast
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class EvaBookAppointmentFragment :
    BaseFragment<FragmentEvaBookAppointmentBinding, EvaBookAppointmentViewModel>(
        EvaBookAppointmentViewModel::class.java
    ) {
    private lateinit var mContext: Context
    private lateinit var lastGeneratedDate: LocalDate
    private var isLoading = false
    private var leadDetail: EvaLeadData? = null
    private var reScheduleAppoinment: Appointment? = null
    private var recordService: EvaRecordAudioService? = null

    private var firstHour: Int = 10
    private var lastHour: Int = 20
    private var slotGap: Int = 60

    private var selectedDate: DateItem? = null
    private var selectedTimeSlot: String = ""
    private var selectedLeadUserName: String = ""
    private var appoinmentMode: String = ""

    private val appointmentDateListAdapter: AppointmentDateListAdapter by lazy {
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
        this.initBundle()
        this.initView()
        this.initListener()
        this.loadLeadList()
        this.loadInitialDates()
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(mContext, EvaRecordAudioService::class.java)
//        mContext.startService(intent)
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (recordService != null) {
            showProgressOfAudio()
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as EvaRecordAudioService.AudioBinder
            recordService = binder.getService()
            showProgressOfAudio()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            recordService = null
        }
    }

    private fun showProgressOfAudio() {
        recordService?.setOnProgressListener { progress, ampl ->
            val hrs = progress / 3600
            val mins = (progress % 3600) / 60
            val secs = progress % 60
            val duration = if (hrs > 0) {
                String.format("%02d:%02d:%02d", hrs, mins, secs)
            } else {
                String.format("%02d:%02d", mins, secs)
            }
            log.d("Recording", "progress: $progress, ampl $ampl")
            binding.incToolbar.tvRecording.visibility = View.VISIBLE
            binding.incToolbar.tvRecording.text = duration
        }
    }

    private fun loadLeadList() {
        lifecycleScope.launch {
            val leads = viewModel.getLeadList().firstOrNull()
            if (leads != null) {
                loadAdapterIntoLeadList(leads)
            }
        }
    }

    private fun loadAdapterIntoLeadList(leadList: List<EvaLeadData>) {
        val leadsName = leadList.map { "${it.firstName} ${it.lastName}" }
        val leadAdapter = ArrayAdapter<String>(
            mContext,
            R.layout.dropdown_text_item,
            leadsName.toMutableList()
        )
        binding.actvLeadDropDown.apply {
            setOnItemClickListener { _, _, position, _ ->
                selectedLeadUserName = leadsName[position]
                leadDetail = leadList[position]
//                if (!leadDetail?.companyName.isNullOrEmpty()) {
//                    binding.llcCompany.visibility = View.GONE
//                }
                binding.etEmail.setText(leadDetail?.email?:"")
                binding.etCompanyName.setText(leadDetail?.companyName?: "")
//                onItemSelected(leadList[position])
            }
            setAdapter(leadAdapter)
        }
    }

    private fun initBundle() {
        if (arguments != null) {
            leadDetail = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments!!.getParcelable("lead_detail", EvaLeadData::class.java)
            } else {
                arguments!!.getParcelable("lead_detail")
            }
            reScheduleAppoinment = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments!!.getParcelable("appointment_detail", Appointment::class.java)
            } else {
                arguments!!.getParcelable("appointment_detail")
            }
            appoinmentMode = arguments!!.getString("appointment_mode", "")
        }
    }


    private fun initView() {
        binding.actvLeadDropDown.setDropDownBackgroundResource(R.color.white)
        this.updateLeadsViewVisibility()
        this.initToolbar()
        this.showDateRecyclerView()
        this.showTimeSlotList()
    }

    private fun updateLeadsViewVisibility() {
        binding.llcLeadDropDown.visibility = if (leadDetail == null) View.VISIBLE else View.GONE
//        binding.llcCompany.visibility = if (leadDetail == null) View.VISIBLE else View.GONE
        if (leadDetail != null) {
            binding.etEmail.setText(leadDetail?.email?:"")
            binding.etCompanyName.setText(leadDetail?.companyName?: "")
        }

        if (reScheduleAppoinment != null) {
            binding.actvLeadDropDown.setText(reScheduleAppoinment?.userName)
            binding.etEmail.setText(reScheduleAppoinment?.userEmail)
            binding.etLocation.setText(reScheduleAppoinment?.location?: "")
            binding.etCompanyName.setText(reScheduleAppoinment?.companyName?: "")
            binding.etSubject.setText(reScheduleAppoinment?.subject?: "")
            if (reScheduleAppoinment!!.appointmentMode == "virtual") {
                binding.rbVirtual.isChecked = true
            } else {
                binding.rbInPerson.isChecked = true
            }
        }
    }

    private fun initToolbar() {
        binding.incToolbar.tvTitle.text = "Book Appointment"
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as EventHostActivity).showHideBottomNavBar(false)
    }

    private fun showDateRecyclerView() {
        appointmentDateListAdapter.onDateSelected = { selectedDate ->
            showTimeSlots(selectedDate)
        }
        binding.rvDatesList.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            adapter = appointmentDateListAdapter

            // Add the scroll listener for dynamic loading
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

//                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                    val visibleItemCount = layoutManager.childCount
//                    val totalItemCount = layoutManager.itemCount
//                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
//
//                    // Logic to detect when the user is near the end of the list
//                    val threshold = 5 // Load when 5 items remain
//                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= (totalItemCount - threshold) && firstVisibleItemPosition >= 0) {
//                        loadMoreDates()
//                    }
                }
            })
        }
    }

    private fun showTimeSlotList() {
        timeSlotAdapter.onTimeSelected = { timeSlot ->
            this.selectedTimeSlot = timeSlot
        }
        binding.rvTimeSlots.apply {
            layoutManager = GridLayoutManager(mContext, 3)
            adapter = timeSlotAdapter
        }
    }

    private fun loadInitialDates() {
        // Load the first chunk (e.g., 30 days)
        val initialList = generateNextDays(lastGeneratedDate, 15, true)
        appointmentDateListAdapter.setDateList(initialList)

        // Update the last generated date for the next chunk
        lastGeneratedDate = initialList.lastOrNull()?.let {
            LocalDate.parse(it.fullDate)
        } ?: LocalDate.now()

        val firstDate = initialList.firstOrNull()
        firstDate?.let {
            showTimeSlots(it)
        }
    }

    private fun generateTimeSlotsForDate(selectedDate: DateItem): List<String> {
        val slots = mutableListOf<String>()
        val now = java.time.LocalTime.now()
        val isToday = selectedDate.fullDate == LocalDate.now().toString()

        var hour = firstHour
        while (hour <= lastHour) {
            var minute = 0
            while (minute < 60) {
                val slotTime = java.time.LocalTime.of(hour, minute)
                // Skip past slots if today
                if (!isToday || slotTime.isAfter(now)) {
                    slots.add(formatTime(slotTime))
                }
                minute += slotGap
            }
            hour += 1
        }
        return slots
    }

    private fun formatTime(time: java.time.LocalTime): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return time.format(formatter)
    }

    private fun initListener() {
        binding.rgMode.setOnCheckedChangeListener { btn, checkBtnId ->
            binding.llcLocation.visibility =
                if (checkBtnId == R.id.rbInPerson) View.VISIBLE else View.GONE
        }
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnSave.setOnClickListener {
            if (validateUserInputs()) {
                saveAppointmentIntoDb()
            }
        }
    }

    private fun saveAppointmentIntoDb() {
        val appointment = Appointment()
        appointment.userName = selectedLeadUserName
        appointment.userEmail = binding.etEmail.text.toString()
        appointment.appointmentMode = if (binding.rbInPerson.isChecked) "person" else "virtual"
        appointment.subject = binding.etSubject.text.toString()
        appointment.appointmentDate = selectedDate?.fullDate
        appointment.appointmentTime = selectedTimeSlot
        appointment.location = binding.etLocation.text.toString()
        if (!leadDetail?.companyName.isNullOrEmpty()) {
            appointment.companyName = leadDetail?.companyName
        } else {
            appointment.companyName = binding.etCompanyName.text.toString()
        }
        if (leadDetail != null) {
            val fullName = "${leadDetail?.firstName} ${leadDetail?.lastName}"
            appointment.userName = fullName
        }

        val fullDateTimeString = "${selectedDate?.fullDate} $selectedTimeSlot"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
        val localDateTime = LocalDateTime.parse(fullDateTimeString, formatter)
        appointment.timestamp = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (appoinmentMode == "from_lead") {
            val audioFile = recordService?.stopRecording()
            val audioFileName = if (leadDetail == null) {
                audioFile?.name
            } else {
                leadDetail!!.audioFilePath
            }
            leadDetail?.audioFilePath = audioFileName
            audioFile?.let {
                recordService?.saveRecordingIntoDb(it, "lead")
            }
            leadDetail?.let {
                viewModel.saveLeadData(it)
            }
        }
        viewModel.saveAppointment(appointment) {
            findNavController().popBackStack()
        }
    }

    private fun validateUserInputs(): Boolean {
        val userEmail = binding.etEmail.text
        if (userEmail.isNullOrEmpty()) {
            mContext.showToast("Opps! Email is empty", ToastType.ERROR)
            return false
        }
        if (selectedTimeSlot.isEmpty()) {
            mContext.showToast("Opps! you forgot appointment time", ToastType.ERROR)
            return false
        }

        return true
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

        Log.d(
            "Scrolling",
            "Loaded ${newDates.size} new dates. Total: ${appointmentDateListAdapter.itemCount}"
        )

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

    private fun showTimeSlots(selectedDate: DateItem) {
        this.selectedDate = selectedDate
        val slots = generateTimeSlotsForDate(selectedDate)
        timeSlotAdapter.setSlotList(slots)
        binding.rvTimeSlots.visibility = if (slots.isNotEmpty()) View.VISIBLE else View.GONE
        binding.tvNoSlots.visibility = if (slots.isNotEmpty()) View.GONE else View.VISIBLE
    }

    companion object {
        fun newInstance() = EvaBookAppointmentFragment()
    }

}