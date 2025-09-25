package com.eva.lead.capture.domain.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "appointments", indices = [Index(value = ["lead_code", "user_id"], unique = true)])
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
)
