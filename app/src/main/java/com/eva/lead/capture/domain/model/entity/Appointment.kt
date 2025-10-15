package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "appointments",
    indices = [Index(value = ["lead_code", "user_id"], unique = true)]
)
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo("user_name")
    var userName: String? = null,
    @ColumnInfo("user_email")
    var userEmail: String? = null,
    @ColumnInfo("appointment_date")
    var appointmentDate: String? = null,
    @ColumnInfo("appointment_time")
    var appointmentTime: String? = null,
    @ColumnInfo("subject")
    var subject: String? = null,
    @ColumnInfo("appointment_mode")
    var appointmentMode: String? = null,
    @ColumnInfo("timestamp")
    var timestamp: Long? = null,
    @ColumnInfo("calendar_uri")
    var calendarUri: String? = null,
    @ColumnInfo("location")
    var location: String? = null,
    @ColumnInfo("isDelete")
    var isDelete: Boolean = false,
)
