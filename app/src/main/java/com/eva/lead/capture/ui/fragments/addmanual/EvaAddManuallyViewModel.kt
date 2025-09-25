package com.eva.lead.capture.ui.fragments.addmanual

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.ui.base.BaseViewModel
import com.eva.lead.capture.utils.ResultWrapper
import com.eva.lead.capture.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EvaAddManuallyViewModel(mContext: Context) : BaseViewModel(mContext) {
    private val _loader = SingleLiveEvent<ResultWrapper.Loading>()
    val loader: LiveData<ResultWrapper.Loading> = _loader

    fun saveLeadData(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        company: String,
        additionalInfo: String
    ) {
        viewModelScope.launch {
            val leadData = EvaLeadData(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phone = phone,
                companyName = company,
                additionalInfo = additionalInfo
            )
            _loader.value = ResultWrapper.Loading
            repositoryDb.insertLead(leadData)
        }
    }
}
