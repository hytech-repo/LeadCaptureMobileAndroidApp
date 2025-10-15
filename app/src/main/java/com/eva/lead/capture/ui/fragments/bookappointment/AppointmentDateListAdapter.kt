package com.eva.lead.capture.ui.fragments.bookappointment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemDateTileBinding

class AppointmentDateListAdapter(val mContext: Context) :
    RecyclerView.Adapter<AppointmentDateListAdapter.EvaDateVH>() {
    var onDateSelected: ((DateItem) -> Unit)? = null
    private var dateList: List<DateItem>? = null
    val currentList: List<DateItem> get() = dateList ?: emptyList()
    private var selectedPosition = 0 // Assuming the first date is selected by default

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvaDateVH {
        val binding = ItemDateTileBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return EvaDateVH(binding)
    }

    override fun onBindViewHolder(holder: EvaDateVH, position: Int) {
        dateList?.get(position)?.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int {
        return dateList?.size ?: 0
    }

    fun setDateList(list: List<DateItem>?) {
        this.dateList = list
        // Reset or set the initial selection here if needed
        // Assuming the list contains an item with isSelected=true or you default to index 0
        if (list != null && list.isNotEmpty()) {
            selectedPosition = list.indexOfFirst { it.isSelected }
            if (selectedPosition == -1) selectedPosition = 0 // Default to first if none marked
            list[selectedPosition].isSelected = true // Ensure the data model is consistent
        } else {
            selectedPosition = -1
        }
        notifyDataSetChanged()
    }

    inner class EvaDateVH(val binding: ItemDateTileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Setup click listener on the root view (the whole tile)
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition != selectedPosition) {

                    // 1. Deselect the previously selected item
                    val oldSelectedPosition = selectedPosition
                    if (oldSelectedPosition != -1 && oldSelectedPosition < dateList?.size ?: 0) {
                        dateList?.get(oldSelectedPosition)?.isSelected = false
                        notifyItemChanged(oldSelectedPosition)
                    }

                    // 2. Select the new item
                    selectedPosition = adapterPosition
                    val newDateItem = dateList!![selectedPosition]
                    newDateItem.isSelected = true
                    notifyItemChanged(selectedPosition) // Refresh the new selection's view
                    onDateSelected?.invoke(newDateItem)
                }
            }
        }

        fun bind(dateItem: DateItem, position: Int) {
            binding.tvDate.text = dateItem.dayNumber
            binding.tvDay.text  = dateItem.dayName

            binding.root.isSelected = dateItem.isSelected
        }

    }
}