package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuestionOption
import com.eva.lead.capture.domain.model.entity.QuestionWithOptions
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaQuestionsViewModel(mcontext: Context) : BaseViewModel(mcontext) {

    fun fetchQuestionWithOptions(type: String): Flow<List<QuestionWithOptions>?> {
        return repositoryDb.getQuestionsWithOptions(type)
    }

    fun saveQuestionIntoList(
        question: String,
        type: String,
        selectedQuestionType: String,
        options: MutableList<String>,
        callback: () -> Unit
    ) {
        viewModelScope.launch {
            val question = QuestionInfo(
                question = question,
                type = type,
                questionType = selectedQuestionType,
                status = 1,
                isMultipleChoice = (selectedQuestionType == "MCQ")
            )
            val questionId = repositoryDb.insertQuestionInfo(question)

            for (option in options) {
                val questionOption = QuestionOption(
                    questionId = questionId,
                    optionText = option
                )
                repositoryDb.insertOption(questionOption)
            }
            callback()
        }

    }

}