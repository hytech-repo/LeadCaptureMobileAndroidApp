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

fun Exhibitor.generateUpdateQuery(): String {
    val updates = mutableListOf<String>()

    this.leadCode.let { updates.add("lead_code = '$it'") }
    this.userId.let { updates.add("user_id = '$it'") }
    this.firstName.let { updates.add("first_name = '$it'") }
    this.lastName.let { updates.add("last_name = '$it'") }
    this.email.let { updates.add("email = '$it'") }
    this.deviceName.let { updates.add("device_name = '$it'") }

    if (updates.isEmpty()) return ""

    val setClause = updates.joinToString(", ")
    return "update exhibitor SET $setClause WHERE lead_code = '${this.leadCode}'"
}

