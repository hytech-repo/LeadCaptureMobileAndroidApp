package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.databinding.ItemMultipleQuestionBinding
import com.eva.lead.capture.databinding.QuestionItemBinding
import com.eva.lead.capture.utils.QuestionTabType


class QuestionsListAdapter(
    private val mContext: Context,
    private val tabType: QuestionTabType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_NORMAL = 0
        private const val TYPE_MULTIPLE = 1
    }

    private var items: List<String> = emptyList()

    fun updateData(list: List<String>) {
        items = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (tabType) {
            QuestionTabType.MY_QUESTIONS, QuestionTabType.QUICK_NOTES -> TYPE_MULTIPLE
            else -> TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MULTIPLE) {
            val binding = ItemMultipleQuestionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            MultipleQuestionViewHolder(binding)
        } else {
            val binding = QuestionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            NormalQuestionViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val questionText = items[position]
        when (holder) {
            is MultipleQuestionViewHolder -> holder.bind(questionText)
            is NormalQuestionViewHolder -> holder.bind(questionText)
        }
    }

    inner class NormalQuestionViewHolder(private val binding: QuestionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: String) {
            binding.tvQuestion.text = question

            // Example: switch default state or onClick
            binding.sGuest.isChecked = false
            binding.root.setOnClickListener {
                // Handle click for normal question
            }
        }
    }

    inner class MultipleQuestionViewHolder(private val binding: ItemMultipleQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: String) {
            binding.tvQuestion.text = question

            // Reset checkboxes (you can customize as needed)
            binding.cbOption1Q1.isChecked = false
            binding.cbOption2Q1.isChecked = false
            binding.cbOption3Q1.isChecked = false
            binding.cbOption4Q1.isChecked = false

            // Setup click listeners or bind real options if you have
            binding.root.setOnClickListener {
                // Handle click for multiple question
            }
        }
    }
}
