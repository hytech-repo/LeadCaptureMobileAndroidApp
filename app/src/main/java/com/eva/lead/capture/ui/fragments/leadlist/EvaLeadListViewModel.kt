package com.eva.lead.capture.ui.fragments.leadlist

import android.content.Context
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class EvaLeadListViewModel(mcontext: Context) : BaseViewModel(mcontext) {
    fun getLeadList(): Flow<List<EvaLeadData>?> {
        return repositoryDb.getAllLeads()
    }
}