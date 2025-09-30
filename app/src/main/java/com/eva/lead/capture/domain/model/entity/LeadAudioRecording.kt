package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audio_recording"/*, indices = [Index(value = ["lead_id"], unique = true)]*/)
data class LeadAudioRecording(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo("name")
    var recordingName: String? = null,
    @ColumnInfo("fileName")
    val tag: String? = null,
    @ColumnInfo("filePath")
    var firstName: String? = null,
    @ColumnInfo("type")
    var type: String? = null,
    @ColumnInfo("is_deleted", defaultValue = "0")
    var isDeleted: Int = 0,
    @ColumnInfo("is_sync", defaultValue = "0")
    var isSync: Int = 0
)
