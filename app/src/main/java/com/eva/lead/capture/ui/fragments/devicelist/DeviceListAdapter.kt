package com.eva.lead.capture.ui.fragments.devicelist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemDeviceBinding
import com.eva.lead.capture.databinding.ItemRecordingBinding
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.utils.formatDuration
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeviceListAdapter(val mContext: Context) :
    RecyclerView.Adapter<DeviceListAdapter.EvaDeviceListVH>() {
    private var deviceList: List<String>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvaDeviceListVH {
        val binding = ItemDeviceBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return EvaDeviceListVH(binding)
    }

    override fun onBindViewHolder(
        holder: EvaDeviceListVH,
        position: Int,
    ) {
        holder.bind(deviceList!![position], position)
    }

    override fun getItemCount(): Int {
        return deviceList?.size ?: 0
    }

    fun setDeviceList(list: List<String>?) {
        this.deviceList = list
        notifyDataSetChanged()
    }

    inner class EvaDeviceListVH(val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: String, position: Int) {
//          binding.nameText.text = model.recordingName
//            binding.dateText.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(model.recordingDate!!))

        }

    }

}