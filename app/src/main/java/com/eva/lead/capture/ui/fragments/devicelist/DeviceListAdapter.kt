package com.eva.lead.capture.ui.fragments.devicelist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ItemDeviceBinding
import com.eva.lead.capture.domain.model.entity.DeviceInfo
import com.eva.lead.capture.utils.toLastLoginString

class DeviceListAdapter(val mContext: Context) :
    RecyclerView.Adapter<DeviceListAdapter.EvaDeviceListVH>() {
    private var deviceList: List<DeviceInfo>? = null

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

    fun setDeviceList(list: List<DeviceInfo>?) {
        this.deviceList = list
        notifyDataSetChanged()
    }

    inner class EvaDeviceListVH(val binding: ItemDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: DeviceInfo, position: Int) {
            binding.tvDeviceName.text = model.deviceName
            binding.tvStatus.text = if (model.isActive) "Active" else "Not Active"
            binding.tvStatus.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (model.isActive) R.color.color_lime_green else R.color.toast_error_bg
                )
            )
            binding.tvCurrent.visibility = if (model.isActive) View.VISIBLE else View.GONE

            val lastLogin = model.timestamp?.toLastLoginString() ?: "N/A"
            binding.tvDateTime.text = "Last Login: $lastLogin"
        }

    }

}