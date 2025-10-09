package com.eva.lead.capture.ui.fragments.profile

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaUserProfileViewModel(mcontext: Context) : BaseViewModel(mcontext) {

    fun checkExhibitor(leadCode: String): Flow<Exhibitor?> {
        return repositoryDb.getExhibitorByLeadCode(leadCode)
    }

    fun getLeadList(): Flow<List<EvaLeadData>?> {
        return repositoryDb.getAllLeads()
    }

    fun clearAllData() {
        viewModelScope.launch {
            repositoryDb.executeRawQuery("delete from lead_data")
            repositoryDb.executeRawQuery("delete from audio_recording")
            repositoryDb.executeRawQuery("delete from device_information")
            repositoryDb.executeRawQuery("delete from question_info where type != 'remote'")
        }
    }

}