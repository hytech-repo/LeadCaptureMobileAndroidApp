package com.eva.lead.capture.ui.fragments.recordingdetail

import android.content.Context
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class EvaRecordingDetailViewModel(mContext: Context) : BaseViewModel(mContext) {


    fun getRecordingDetail(id : String): Flow<LeadAudioRecording?> {
        return repositoryDb.getRecordingById(id)
    }
}