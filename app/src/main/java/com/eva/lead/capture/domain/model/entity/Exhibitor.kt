package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exhibitor", indices = [Index(value = ["lead_code", "user_id"], unique = true)])
data class Exhibitor(
    @PrimaryKey(autoGenerate = false)
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

val leadIds = listOf(
    "LC001", "LC002", "LC003", "LC004", "LC005", "LC006",
    "LC007", "LC008", "LC009", "LC010", "LC011", "LC012",
    "LC013"
)
val firstNames = listOf(
    "Rajeev", "Damnish", "Neeraj", "Pramil", "Matt", "Gaurav",
    "Darrean", "Jason", "Gil", "Avin", "Mehdi", "Murali",
    "Manoj"
)
val lastNames = listOf(
    "Gupta", "Kumar", "Garg", "Verma", "Peterson", "Chawla",
    "Janes", "Luu", "Gonzalez", "Kumar", "Raza", "Puttaparthi",
    "Vyas"
)
val emails = listOf(
    "rg@aplusify.com", "dk@maplelms.com", "garg@hytechpro.com", "pramil@maplelms.com",
    "matt@maplelms.com", "gaurav@ablypro.com", "darrean@maplelms.com", "jason.l@ablypro.com",
    "gil@evareg.com", "avi@maplelms.com", "mehdi@evareg.com", "murali@ablypro.com",
    "manoj.vyas@hytechpro.com"
)

val exhibitors = leadIds.mapIndexed { index, leadCode ->
    Exhibitor(
        leadCode = leadCode,
        userId = "U_00${index + 1}", // example userId
        firstName = firstNames.getOrNull(index),
        lastName = lastNames.getOrNull(index),
        email = emails.getOrNull(index),
//        deviceName = "Device_${index + 1}" // example device name
    )
}

