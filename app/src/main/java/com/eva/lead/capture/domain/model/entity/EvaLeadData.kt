package com.eva.lead.capture.domain.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "lead_data", indices = [Index(value = ["lead_id"], unique = true)])
data class EvaLeadData(
    @PrimaryKey(autoGenerate = false)
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
    @ColumnInfo("images")
    var imageFileNames: String? = null,
    @ColumnInfo("audio_fileId")
    var audioFilePath: String? = null,
    @ColumnInfo("timestamp")
    var timestamp: Long? = null,
    @ColumnInfo("quicknote")
    var quickNote: String? = null,
    @ColumnInfo("questionAnswer")
    var questionAnswer: String? = null,
    @ColumnInfo("is_deleted", defaultValue = "0")
    var isDeleted: Int = 0,
    @ColumnInfo("is_sync", defaultValue = "0")
    var isSync: Int = 0
): Parcelable