package com.eva.lead.capture.ui.fragments.question

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuestionOption
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class EvaQuestionsViewModel(mcontext: Context) : BaseViewModel(mcontext) {
    fun saveQuestionIntoList(
        question: String,
        selectedQuestionType: String,
        options: MutableList<String>
    ) {
        viewModelScope.launch {
            val question = QuestionInfo(
                question = question,
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
        }

    }

}