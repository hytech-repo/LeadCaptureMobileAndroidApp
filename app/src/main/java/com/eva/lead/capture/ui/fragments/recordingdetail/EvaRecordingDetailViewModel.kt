package com.eva.lead.capture.ui.fragments.recordingdetail

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaRecordingDetailViewModel(mContext: Context) : BaseViewModel(mContext) {

    fun getRecordingDetail(id : String): Flow<LeadAudioRecording?> {
        return repositoryDb.getRecordingById(id)
    }

    fun getLeadList(): Flow<List<EvaLeadData>?> {
        return repositoryDb.getAllLeads()
    }

    fun updateLeadData(leadData: EvaLeadData) {
        viewModelScope.launch {
//            _loader.value = ResultWrapper.Loading
            repositoryDb.updateLead(leadData)
        }
    }
}