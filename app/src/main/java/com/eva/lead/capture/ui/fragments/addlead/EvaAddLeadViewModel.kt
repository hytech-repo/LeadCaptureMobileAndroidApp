package com.eva.lead.capture.ui.fragments.addlead

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class EvaAddLeadViewModel(mcontext: Context) : BaseViewModel(mcontext) {

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