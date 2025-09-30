package com.eva.lead.capture.domain.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "question_info", indices = [Index(value = ["server_id"], unique = true)])
data class QuestionInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo("server_id")
    var serverId: Int? = null,
    @ColumnInfo("question_text")
    var question: String? = null,
    @ColumnInfo(name = "question_type")
    var questionType: String, // e.g., "MCQ", "ShortAnswer"
    @ColumnInfo(name = "is_multiple_choice")
    var isMultipleChoice: Boolean? = null,
    @ColumnInfo("status")
    var status: Int = 1,
    @ColumnInfo("is_deleted", defaultValue = "0")
    var isDeleted: Int = 0,
)

@Entity(
    tableName = "question_options",
    foreignKeys = [
        ForeignKey(
            entity = QuestionInfo::class,
            parentColumns = ["id"],
            childColumns = ["question_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["question_id"])]
)
data class QuestionOption(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "option_serverId")
    var serverId: String? = null, // Nullable if not synced yet

    @ColumnInfo(name = "question_id")
    var questionId: Long = 0, // Foreign key referring to QuestionInfo

    @ColumnInfo(name = "option_text")
    var optionText: String
)
