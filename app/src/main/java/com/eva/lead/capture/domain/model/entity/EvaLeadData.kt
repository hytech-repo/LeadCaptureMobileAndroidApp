package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "lead_data", indices = [Index(value = ["lead_id"], unique = true)])
data class EvaLeadData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo("lead_id")
    var leadId: String? = null,
    @ColumnInfo("tag")
    val tag: String? = null,
    @ColumnInfo("first_name")
    var firstName: String? = null,
    @ColumnInfo("last_name")
    var lastName: String? = null,
    @ColumnInfo("email")
    var email: String? = null,
    @ColumnInfo("phone")
    var phone: String? = null,
    @ColumnInfo("designation")
    var designation: String? = null,
    @ColumnInfo("company_name")
    var companyName: String? = null,
    @ColumnInfo("additional_info")
    var additionalInfo: String? = null,
    @ColumnInfo("notes")
    var notes: String? = null,
    @ColumnInfo("audio_file_path")
    var audioFilePath: String? = null,
    @ColumnInfo("timestamp")
    var timestamp: Long? = null,
    @ColumnInfo("is_deleted", defaultValue = "0")
    var isDeleted: Int = 0,
    @ColumnInfo("is_sync", defaultValue = "0")
    var isSync: Int = 0
)



val lead1 = EvaLeadData(
    leadId = "L001",
    tag = "hot",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    phone = "18927876",
    designation = "Software Developer",
    companyName = "EVA",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)
val lead2 = EvaLeadData(
    leadId = "L002",
    tag = "hot",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    designation = "Software Engineer",
    phone = "18927876",
    companyName = "Melimu",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)

val lead3 = EvaLeadData(
    leadId = "L003",
    tag = "hot",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    designation = "Professional",
    phone = "18927876",
    companyName = "Maple",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)

val lead4 = EvaLeadData(
    leadId = "L004",
    tag = "hot",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    designation = "Manager",
    phone = "18927876",
    companyName = "EVA",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)


val lead5 = EvaLeadData(
    leadId = "L005",
    tag = "hot",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    phone = "18927876",
    companyName = "EVA",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)


val lead6 = EvaLeadData(
    leadId = "L006",
    tag = "medium",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    designation = "Software Tester",
    phone = "18927876",
    companyName = "Hytech",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)

val lead7 = EvaLeadData(
    leadId = "L007",
    tag = "medium",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    designation = "Quality Analyst",
    phone = "18927876",
    companyName = "Aplsfy",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)

val lead8 = EvaLeadData(
    leadId = "L008",
    tag = "medium",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    phone = "18927876",
    companyName = "Melimu",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)
val lead9 = EvaLeadData(
    leadId = "L009",
    tag = "cold",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    designation = "Tech Lead",
    phone = "18927876",
    companyName = "Hytech",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)

val lead10 = EvaLeadData(
    leadId = "L010",
    tag = "cold",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    designation = "Principal Engineer",
    phone = "18927876",
    companyName = "Eva",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)

val lead11 = EvaLeadData(
    leadId = "L011",
    tag = "cold",
    firstName = "Anderson",
    lastName = "James",
    email = "anderson@invalid.com",
    phone = "18927876",
    companyName = "Maple",
    additionalInfo = "Hello",
    notes = "Please wait for this email",
    timestamp = System.currentTimeMillis(),
    audioFilePath = "",
    isDeleted = 0,
    isSync = 0,
)