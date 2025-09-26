package com.eva.lead.capture.ui.fragments.leadform

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemAddPhotoBinding
import com.eva.lead.capture.databinding.ItemMediaBinding

class EvaAttachedMediaAdapter(val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var mList: List<String>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val binding = ItemAddPhotoBinding.inflate(LayoutInflater.from(mContext), parent, false)
            return EvaAddMediaVH(binding)
        } else {
            val binding = ItemMediaBinding.inflate(LayoutInflater.from(mContext), parent, false)
            return EvaMediaVH(binding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (holder is EvaMediaVH) {

        } else if (holder is EvaAddMediaVH) {

        }
    }

    override fun getItemCount(): Int = (mList?.size?: 0) + 1

    override fun getItemViewType(position: Int): Int {
        if (mList.isNullOrEmpty()) {
            return 1
        } else {
            if (mList!!.size == position) {
                return 1
            } else {
                return 0
            }
        }
    }

    inner class EvaMediaVH(binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root)

    inner class EvaAddMediaVH(binding: ItemAddPhotoBinding) : RecyclerView.ViewHolder(binding.root)
}