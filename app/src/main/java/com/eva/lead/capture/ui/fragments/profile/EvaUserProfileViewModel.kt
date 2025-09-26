package com.eva.lead.capture.ui.fragments.profile

import android.content.Context
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class EvaUserProfileViewModel(mcontext: Context) : BaseViewModel(mcontext) {

    fun checkExhibitor(leadCode: String): Flow<Exhibitor?> {
        return repositoryDb.getExhibitorByLeadCode(leadCode)
    }
}