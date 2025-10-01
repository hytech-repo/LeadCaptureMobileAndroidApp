package com.eva.lead.capture.ui.fragments.leadlist

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ItemLeadBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.utils.getDrawableStatus
import com.eva.lead.capture.utils.getStatusColor
import java.util.Locale

class EvaLeadListAdapter(val mContext: Context) :
    RecyclerView.Adapter<EvaLeadListAdapter.EvaLeadListVH>() {
    private val colorResArray =
        arrayListOf<Int>(
            R.color.color_lime_green,
            R.color.status_yellow,
            R.color.color_light_blue,
            R.color.color_purple,
            R.color.color_pinkish_red
        )

    private var list: List<EvaLeadData>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvaLeadListVH {
        val binding = ItemLeadBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return EvaLeadListVH(binding)
    }

    override fun onBindViewHolder(
        holder: EvaLeadListVH,
        position: Int
    ) {
        val model = list!![position]
        holder.bind(model, position)
    }

    override fun getItemCount(): Int = list?.size ?: 0

    fun setLeadDataList(list: List<EvaLeadData>?) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class EvaLeadListVH(val binding: ItemLeadBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: EvaLeadData, position: Int) {
            val colorRes = colorResArray[position % 5]
            val color = ContextCompat.getColor(mContext, colorRes)
            val bgColor = ColorUtils.setAlphaComponent(color, 15)
            val firstNameChar = model.firstName?.getOrNull(0)?.uppercaseChar() ?: ""
            binding.avatarText.text = "$firstNameChar"
            binding.avatarText.setTextColor(color)
            binding.avatarText.backgroundTintList = ColorStateList.valueOf(bgColor)
            binding.nameText.text = "${model.firstName} ${model.lastName}"
            val text = if (!model.designation.isNullOrEmpty()) {
                model.designation
            } else if (!model.companyName.isNullOrEmpty()) {
                model.companyName
            } else {
                ""
            }
            binding.roleText.text = text

            val imageRes = if (model.tag == "hot") {
                R.drawable.ic_thunder
            } else if (model.tag == "medium") {
                R.drawable.ic_fire
            } else {
                R.drawable.ic_cold
            }
            val textColor = ContextCompat.getColor(mContext, model.tag!!.getStatusColor())
            binding.llcHotLabel.background = mContext.getDrawableStatus(model.tag)
            binding.hotLabel.setTextColor(textColor)
            binding.icTag.setImageResource(imageRes)
            binding.hotLabel.text = model.tag.capitalize(Locale.ENGLISH)
        }
    }
}