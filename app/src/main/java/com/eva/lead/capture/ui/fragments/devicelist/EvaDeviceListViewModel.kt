package com.eva.lead.capture.ui.fragments.devicelist

import android.content.Context
import com.eva.lead.capture.domain.model.entity.DeviceInfo
import com.eva.lead.capture.ui.base.BaseViewModel
import kotlinx.coroutines.flow.Flow

class EvaDeviceListViewModel(val mcontext: Context): BaseViewModel(mcontext) {

    fun getDeviceList(): Flow<List<DeviceInfo>?> {
        return repositoryDb.getAllDevices()
    }

}