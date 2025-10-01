package com.eva.lead.capture.ui.fragments.help

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ItemFaqHelpBinding
import com.eva.lead.capture.domain.model.FAQHelpItem

class EvaHelpFaqAdapter(val mcontext: Context) :
    RecyclerView.Adapter<EvaHelpFaqAdapter.EvaHelpFaqVH>() {

    private var items: List<FAQHelpItem>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EvaHelpFaqVH {
        val binding = ItemFaqHelpBinding.inflate(LayoutInflater.from(mcontext), parent, false)
        return EvaHelpFaqVH(binding)
    }

    override fun onBindViewHolder(
        holder: EvaHelpFaqVH,
        position: Int
    ) {
        val item = items!![position]
        holder.binding.tvQuestion.text = item.question
        holder.binding.tvAnswer.text = item.answer
        holder.binding.tvAnswer.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        holder.binding.ivToggle.setImageResource(if (item.isExpanded) R.drawable.ic_minus else R.drawable.ic_add)

        holder.binding.tvQuestion.setOnClickListener {
            // Toggle expand/collapse
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
        }
        holder.binding.ivToggle.setOnClickListener {
            // Same toggle logic if clicked on icon
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    fun setFaqItems(items: List<FAQHelpItem>?) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class EvaHelpFaqVH(val binding: ItemFaqHelpBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}