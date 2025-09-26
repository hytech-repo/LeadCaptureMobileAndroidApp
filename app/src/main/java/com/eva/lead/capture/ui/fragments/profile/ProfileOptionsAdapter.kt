package com.eva.lead.capture.ui.fragments.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ItemProfileOptionBinding

class ProfileOptionsAdapter(private val mContext: Context) :
    RecyclerView.Adapter<ProfileOptionsAdapter.ViewHolder>() {
    private var options: List<ProfileOption>? = null
    var onItemClick: (ProfileOption, Int) -> Unit = { data: ProfileOption, position: Int -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProfileOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = options?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options!![position], position)
    }

    fun updateProfileOption(list: List<ProfileOption>) {
        this.options = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemProfileOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(option: ProfileOption, position: Int) {
            binding.ivIcon.apply {
                setImageResource(option.iconResId)
                setColorFilter(
                    ContextCompat.getColor(context, R.color.subheading_text_color),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            binding.tvLabel.text = option.label

            binding.root.setOnClickListener {
                onItemClick(option, position)
            }
        }
    }
}
