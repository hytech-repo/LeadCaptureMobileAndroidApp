package com.eva.lead.capture.ui

import android.app.Application
import com.eva.lead.capture.R
import com.eva.lead.capture.constants.AppConstants
import com.eva.lead.capture.data.local.AppDatabase
import com.eva.lead.capture.data.repository.AppDbRepositoryImpl
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.dummyUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray

class EvaLeadCaptureApp : Application() {

    override fun onCreate() {
        super.onCreate()

        this.insertSomePredefinedDataIntoDb()

    }

    private fun insertSomePredefinedDataIntoDb() {
        CoroutineScope(Dispatchers.IO).launch {
            val appDatabase = AppDatabase.getInstance(this@EvaLeadCaptureApp)
            val appDbRepository = AppDbRepositoryImpl(appDatabase)
            appDbRepository.insertExhibitor(dummyUser)

            insertPreDefineQuestion(appDbRepository)
        }
    }

    private suspend fun insertPreDefineQuestion(appDbRepository: AppDbRepositoryImpl) {
        val inputStream = resources.openRawResource(R.raw.questions)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val jsonArray = JSONArray(jsonString)
        val questionList = mutableListOf<QuestionInfo>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            val serverId = json.getString("id")
            val text = json.getString("question")
            val questionType = json.getString("question_type")
            val options = json.getJSONArray("options")
            val optionList = mutableListOf<String>()
            for (j in 0 until options.length()) {
                optionList.add(options.getString(j))
            }
            val isMultiple = questionType != AppConstants.SINGLE_CHOICE
            val question = QuestionInfo(
                serverId = serverId.toInt(),
                question = text,
                questionType = "choice",
                type = "remote",
                isMultipleChoice = isMultiple,
                status = 1,
                options = optionList
            )
            questionList.add(question)
        }
        appDbRepository.insertQuestionInfos(questionList)
    }
}