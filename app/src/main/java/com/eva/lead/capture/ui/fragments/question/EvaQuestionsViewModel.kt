package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaQuestionsViewModel(mcontext: Context) : BaseViewModel(mcontext) {

    fun fetchQuestionWithOptions(type: String): Flow<List<QuestionInfo>?> {
        return repositoryDb.getQuestionsWithOptions(type)
    }

    fun saveQuestionIntoList(
        question: String,
        type: String,
        selectedQuestionType: Int,
        options: MutableList<String>,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val questionType = when (selectedQuestionType) {
                0, 1 -> "choice"
                2 -> "text"
                else -> ""
            }

            val question = QuestionInfo(
                question = question,
                type = type,
                questionType = questionType,
                status = 1,
                options = options,
                isMultipleChoice = (selectedQuestionType == 1)
            )
            repositoryDb.insertQuestionInfo(question)
            callback()
        }

    }

    fun updateQuestionIntoDb(question: QuestionInfo, callback: (() -> Unit)? = null) {
        viewModelScope.launch {
            repositoryDb.updateQuestionInfo(question)
            callback?.invoke()
        }
    }

}