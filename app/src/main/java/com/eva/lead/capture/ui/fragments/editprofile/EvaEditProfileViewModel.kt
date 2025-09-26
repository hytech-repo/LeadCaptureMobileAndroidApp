package com.eva.lead.capture.ui.fragments.editprofile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.domain.model.entity.generateUpdateQuery
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class EvaEditProfileViewModel(mContext: Context) : BaseViewModel(mContext) {

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> get() = _saveSuccess

    fun saveUserData(
        firstName: String,
        lastName: String,
        email: String,
        deviceName: String,
        leadCode: String,
    ) {
        viewModelScope.launch {
            try {
                val exhibitorInfo = Exhibitor(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    deviceName = deviceName,
                    leadCode = leadCode
                )
//                repositoryDb.updateExhibitor(exhibitorInfo)
                val query = exhibitorInfo.generateUpdateQuery()
                repositoryDb.executeRawQuery(query)
                _saveSuccess.postValue(true)
            } catch (e: Exception) {
                _saveSuccess.postValue(false)
            }
        }
    }


    suspend fun saveUpdateExhibitorDetailIntoDb() {

    }
}