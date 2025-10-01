package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.eva.lead.capture.R
import com.eva.lead.capture.databinding.ItemMultipleQuestionBinding
import com.eva.lead.capture.databinding.QuestionItemBinding
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.utils.QuestionTabType


private const val TYPE_NORMAL = 0
private const val TYPE_MULTIPLE = 1

class QuestionsListAdapter(
    private val mContext: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<QuestionInfo> = emptyList()
    private var tabType: QuestionTabType = QuestionTabType.QUESTIONS

    var onItemClickListener: (view: View, data: QuestionInfo, position: Int) -> Unit = { view, data, position -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_MULTIPLE) {
            val binding = ItemMultipleQuestionBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
            MultipleQuestionViewHolder(binding)
        } else {
            val binding = QuestionItemBinding.inflate(
                LayoutInflater.from(mContext),
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

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (tabType) {
            QuestionTabType.MY_QUESTIONS, QuestionTabType.QUICK_NOTES -> TYPE_MULTIPLE
            else -> TYPE_NORMAL
        }
    }

    fun updateData(list: List<QuestionInfo>, tabType: QuestionTabType) {
        this.items = list
        this.tabType = tabType
        notifyDataSetChanged()
    }

    inner class NormalQuestionViewHolder(private val binding: QuestionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionInfo) {
            binding.tvQuestion.text = question.question

            binding.sGuest.isChecked = false
            binding.root.setOnClickListener {
                onItemClickListener.invoke(it, question, absoluteAdapterPosition)
            }
        }
    }

    inner class MultipleQuestionViewHolder(private val binding: ItemMultipleQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: QuestionInfo) {
            val questionInfo = question.question
            if (questionInfo != null) {
                binding.tvQuestion.text = questionInfo
                val options = question.options
                if (!options.isNullOrEmpty()) {
                    binding.llcOptions.removeAllViews()
                    for (option in options) {
                        val checkBox = CheckBox(mContext)
                        checkBox.text = option
                        val color = ContextCompat.getColor(mContext, R.color.text_color_grey)
                        checkBox.typeface = ResourcesCompat.getFont(mContext, R.font.sf_pro_regular)
                        checkBox.buttonTintList = ColorStateList.valueOf(color)
                        binding.llcOptions.addView(checkBox)
                    }
                }
            }

            // Setup click listeners or bind real options if you have
            binding.ivOptionMenuDot.setOnClickListener {
                onItemClickListener.invoke(it, question, absoluteAdapterPosition)
            }
        }
    }
}
