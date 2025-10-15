package com.eva.lead.capture.ui.fragments.appointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemAppointmentBinding
import com.eva.lead.capture.domain.model.entity.Appointment

class EvaAppointmentAdapter(val mContext: Context): RecyclerView.Adapter<EvaAppointmentAdapter.EvaAppointmentVH>() {

    private var appointmentList: List<Appointment>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvaAppointmentVH {
        val binding = ItemAppointmentBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return EvaAppointmentVH(binding)
    }

    override fun onBindViewHolder(
        holder: EvaAppointmentVH,
        position: Int
    ) {
        val model = appointmentList!![position]
    }

    override fun getItemCount(): Int = appointmentList?.size?: 0

    inner class EvaAppointmentVH(val binding: ItemAppointmentBinding): RecyclerView.ViewHolder(binding.root) {

    }
}