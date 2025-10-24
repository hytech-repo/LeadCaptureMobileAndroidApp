package com.eva.lead.capture.ui.fragments.bookappointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemRadioBtnBinding

class AppointmentTimeSlotAdapter(val mContext: Context) :
    RecyclerView.Adapter<AppointmentTimeSlotAdapter.AppointmentTimeSlotVH>() {

    var onTimeSelected: ((String) -> Unit)? = null
    private var slotList: List<String> = emptyList()
    private var selectedPosition = -1

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
        holder.bind(slotList[position], position)
    }

    override fun getItemCount(): Int = slotList.size?: 0

    fun setSlotList(list: List<String>) {
        this.slotList = list
        selectedPosition = -1
        notifyDataSetChanged()
    }

    inner class AppointmentTimeSlotVH(val binding: ItemRadioBtnBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(slot: String, position: Int) {
            binding.rbTimeSlot.text = slot
            binding.rbTimeSlot.isChecked = (position == selectedPosition)

            binding.rbTimeSlot.setOnClickListener {
                val oldPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)
                onTimeSelected?.invoke(slotList[selectedPosition])
            }

        }

    }
}