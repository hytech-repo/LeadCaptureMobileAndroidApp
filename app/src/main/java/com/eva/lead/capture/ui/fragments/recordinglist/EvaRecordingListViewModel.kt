package com.eva.lead.capture.ui.fragments.recordinglist

import android.content.Context
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class EvaRecordingListViewModel(mContext: Context) : BaseViewModel(mContext) {

    fun getRecordingList(): Flow<List<LeadAudioRecording>?> {
        return repositoryDb.getAllRecording()
    }


}