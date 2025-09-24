package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exhibitor", indices = [Index(value = ["lead_code", "user_id"], unique = true)])
data class Exhibitor(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo("lead_code")
    var leadCode: String? = null,
    @ColumnInfo("user_id")
    val userId: String? = null,
    @ColumnInfo("first_name")
    var firstName: String? = null,
    @ColumnInfo("last_name")
    var lastName: String? = null,
    @ColumnInfo("email")
    var email: String? = null,
    @ColumnInfo("device_name")
    var deviceName: String? = null
)

val dummyUser = Exhibitor(
    leadCode = "LC001",
    userId = "U001",
    firstName = "Laxmi",
    lastName = "Kant",
    email = "lakshmikant@evareg.com"
)
