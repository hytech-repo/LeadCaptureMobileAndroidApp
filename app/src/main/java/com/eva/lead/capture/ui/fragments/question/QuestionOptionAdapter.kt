package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.QuestionOptionBinding
import com.eva.lead.capture.ui.fragments.profile.ProfileOption

class QuestionOptionAdapter(private val mContext: Context) :
    RecyclerView.Adapter<QuestionOptionAdapter.ViewHolder>() {
    private var options: MutableList<String>? = null
    var onItemClick: (ProfileOption, Int) -> Unit = { data: ProfileOption, position: Int -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = QuestionOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = options?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options!![position], position)
    }

    fun updateQuestionList(list: MutableList<String>) {
        this.options = list
        notifyDataSetChanged()
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (options!!.size >= 2) {
            val movedItem = options!!.removeAt(fromPosition)
            options!!.add(toPosition, movedItem)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    fun removeItem(position: Int) {
        options!!.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ViewHolder(private val binding: QuestionOptionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: String, position: Int) {
            binding.tvOption.text = question
        }
    }
}
