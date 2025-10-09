package com.eva.lead.capture.ui.fragments.leadform

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuickNote
import com.eva.lead.capture.ui.base.BaseViewModel
import com.eva.lead.capture.utils.ResultWrapper
import com.eva.lead.capture.utils.SingleLiveEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaLeadFormViewModel(mContext: Context) : BaseViewModel(mContext) {
    private val _loader = SingleLiveEvent<ResultWrapper.Loading>()
    val loader: LiveData<ResultWrapper.Loading> = _loader

    fun saveLeadData(leadData: EvaLeadData) {
        viewModelScope.launch {
//            _loader.value = ResultWrapper.Loading
            repositoryDb.insertLead(leadData)
        }
    }

    fun updateLeadData(leadData: EvaLeadData) {
        viewModelScope.launch {
//            _loader.value = ResultWrapper.Loading
            repositoryDb.updateLead(leadData)
        }
    }


    fun fetchQuestions(type: String): Flow<List<QuestionInfo>?> {
        return repositoryDb.getActiveQuestions(type)
    }

    fun getActiveQuickNote(): Flow<List<QuickNote>?> {
        return repositoryDb.getActiveQuickNotes()
    }

}
