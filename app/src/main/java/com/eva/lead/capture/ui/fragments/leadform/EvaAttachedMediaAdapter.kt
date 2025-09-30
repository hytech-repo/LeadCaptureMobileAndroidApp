package com.eva.lead.capture.ui.fragments.leadform

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eva.lead.capture.databinding.ItemAddPhotoBinding
import com.eva.lead.capture.databinding.ItemMediaBinding
import java.io.File

class EvaAttachedMediaAdapter(val mContext: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: List<String>? = null
    internal var onItemClickListener: (path: String, action: String, position: Int) -> Unit = { path, action, position -> }

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
            holder.bind(position)
        } else if (holder is EvaAddMediaVH) {
            holder.bind()
        }
    }

    fun setList(mediaList: List<String>?) {
        this.mList = mediaList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = (mList?.size ?: 0) + 1

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

    inner class EvaMediaVH(val binding: ItemMediaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val path = mList!![position]
            val file = File(path)
            Glide.with(mContext).load(file).into(binding.ivEventLogo)
            binding.ivCross.setOnClickListener {
                onItemClickListener.invoke(path, "remove", position)
            }
            binding.clSelectedImage.setOnClickListener {
                onItemClickListener.invoke(path, "select", position)
            }
        }
    }

    inner class EvaAddMediaVH(val binding: ItemAddPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener {
                onItemClickListener.invoke("", "add", 0)
            }
        }
    }
}