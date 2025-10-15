package com.eva.lead.capture.ui.fragments.bookappointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemRadioBtnBinding

class AppointmentTimeSlotAdapter(val mContext: Context) :
    RecyclerView.Adapter<AppointmentTimeSlotAdapter.AppointmentTimeSlotVH>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentTimeSlotVH {
        val binding = ItemRadioBtnBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return AppointmentTimeSlotVH(binding)
    }

    override fun onBindViewHolder(
        holder: AppointmentTimeSlotVH,
        position: Int
    ) {
    }

    override fun getItemCount(): Int = 6

    inner class AppointmentTimeSlotVH(val binding: ItemRadioBtnBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}