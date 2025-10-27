package com.eva.lead.capture.ui.fragments.appointmentdetail

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.FragmentEvaAppointmentDetailBinding
import com.eva.lead.capture.domain.model.entity.Appointment
import com.eva.lead.capture.ui.base.BaseFragment
import com.eva.lead.capture.utils.convertIntoDate

class EvaAppointmentDetailFragment :
    BaseFragment<FragmentEvaAppointmentDetailBinding, EvaAppointmentDetailViewModel>(
        EvaAppointmentDetailViewModel::class.java
    ) {

    companion object {
        fun newInstance() = EvaAppointmentDetailFragment()
    }

    private lateinit var mContext: Context
    private var appointment: Appointment? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEvaAppointmentDetailBinding {
        return FragmentEvaAppointmentDetailBinding.inflate(inflater, container, false)
    }

    override fun startWorking(savedInstanceState: Bundle?) {
        this.initBundle()
        this.initToolbar()
        this.initView()
        this.initListener()
    }

    private fun initBundle() {
        if (arguments != null) {
            appointment = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments!!.getParcelable("appointment_detail", Appointment::class.java)
            } else {
                arguments!!.getParcelable("appointment_detail")
            }
        }
    }

    private fun initToolbar() {
        binding.incToolbar.tvTitle.text = "Appointment Detail"
        binding.incToolbar.ivOptions.visibility = View.VISIBLE
    }

    private fun initView() {
        binding.tvSubDesc.text = appointment?.subject ?: ""
        val date = appointment?.timestamp?.convertIntoDate("dd MM yyyy")
        val time = appointment?.timestamp?.convertIntoDate("hh:mm a")
        binding.tvDate.text = date
        binding.tvTime.text = time
        binding.lead.text = appointment?.userName
        binding.leadEmail.text = appointment?.userEmail
        if (appointment?.appointmentMode == "virtual") {
            binding.llcZoomMeeting.visibility = View.VISIBLE
            binding.llcLocation.visibility = View.GONE
        } else {
            binding.llcZoomMeeting.visibility = View.GONE
            binding.llcLocation.visibility = View.VISIBLE
            binding.tvLocation.text = appointment?.location
        }
    }

    private fun initListener() {
        binding.incToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.incToolbar.ivOptions.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(mContext, view)
        // Inflate menu from XML
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.appointment_popup, popupMenu.menu)

        popupMenu.menu.findItem(R.id.menu_view).isVisible = false

        popupMenu.show()
    }
}