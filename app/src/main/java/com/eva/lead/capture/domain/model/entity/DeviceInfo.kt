package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "device_information", indices = [Index(value = ["device_id"], unique = true)])
data class DeviceInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo("device_id")
    var deviceId: String? = null,
    @ColumnInfo("device_name")
    var deviceName: String? = null,
    @ColumnInfo("is_active")
    var isActive: Boolean = false,
    @ColumnInfo("timestamp")
    var timestamp: Long? = null,
)