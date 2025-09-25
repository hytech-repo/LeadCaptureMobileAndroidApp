package com.eva.lead.capture.ui.fragments.leadlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemLeadBinding
import com.eva.lead.capture.domain.model.entity.EvaLeadData

class EvaLeadListAdapter(val mContext: Context) :
    RecyclerView.Adapter<EvaLeadListAdapter.EvaLeadListVH>() {

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

    }

    override fun getItemCount(): Int = list?.size ?: 0

    fun setLeadDataList(list: List<EvaLeadData>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class EvaLeadListVH(val binding: ItemLeadBinding) : RecyclerView.ViewHolder(binding.root)
}