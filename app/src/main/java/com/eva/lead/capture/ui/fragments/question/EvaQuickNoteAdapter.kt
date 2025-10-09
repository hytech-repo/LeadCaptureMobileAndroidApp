package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemQuickOptionBinding
import com.eva.lead.capture.domain.model.entity.QuickNote

class EvaQuickNoteAdapter(val mContext: Context) :
    RecyclerView.Adapter<EvaQuickNoteAdapter.QuickNoteVH>() {
    private var quickNoteList: List<QuickNote>? = null
    var onItemClickListener: (data: QuickNote, action: String) -> Unit =
        { data, action -> }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuickNoteVH {
        val binding = ItemQuickOptionBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return QuickNoteVH(binding)
    }

    override fun onBindViewHolder(
        holder: QuickNoteVH,
        position: Int
    ) {
        val model = quickNoteList!![position]
        holder.bind(model, position)
    }

    override fun getItemCount(): Int = quickNoteList?.size ?: 0
    fun setQuickNoteList(quickNoteList: List<QuickNote>) {
        this.quickNoteList = quickNoteList
        notifyDataSetChanged()
    }

    inner class QuickNoteVH(private val binding: ItemQuickOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: QuickNote, position: Int) {
            binding.ivDelete.isEnabled = quickNoteList!!.size > 2
            binding.cbOption.text = note.text
            binding.switchToggle.isChecked = note.status == 1
            binding.ivDelete.setOnClickListener {
                note.isDeleted = true
                onItemClickListener.invoke(note, "delete")
            }
            binding.switchToggle.setOnClickListener {
                val check = binding.switchToggle.isChecked
                val status = if (check) 1 else 0
                note.status = status
                onItemClickListener.invoke(note, "update")
            }
        }
    }

}