package com.eva.lead.capture.domain.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "question_info", indices = [Index(value = ["server_id"], unique = true)])
data class QuestionInfo(
    @PrimaryKey(autoGenerate = false)
    var id: Long? = null,
    @ColumnInfo("server_id")
    var serverId: Int? = null,
    @ColumnInfo("question_text")
    var question: String? = null,
    @ColumnInfo(name = "type")
    var type: String? = null, // e.g. Quick Note or Question
    @ColumnInfo(name = "question_type")
    var questionType: String? = null, // e.g. "MCQ", "ShortAnswer"
    @ColumnInfo(name = "is_multiple_choice")
    var isMultipleChoice: Boolean? = null,
    @ColumnInfo("status")
    var status: Int = 1,
    @ColumnInfo("options")
    var options: List<String>? = null,
    @ColumnInfo("is_deleted", defaultValue = "0")
    var isDeleted: Int = 0,
): Parcelable

@Entity(tableName = "quick_note", indices = [Index(value = [/*"server_id",*/ "text"], unique = true)])
data class QuickNote(
    @PrimaryKey(autoGenerate = false)
    var id: Long? = null,
    @ColumnInfo("server_id")
    var serverId: Int? = null,
    @ColumnInfo("status")
    var status: Int = 1,
    @ColumnInfo("text")
    var text: String? = null,
    @ColumnInfo("is_deleted")
    var isDeleted: Boolean = false
)

//@Entity(
//    tableName = "question_options",
//    foreignKeys = [
//        ForeignKey(
//            entity = QuestionInfo::class,
//            parentColumns = ["id"],
//            childColumns = ["question_id"],
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
//    indices = [Index(value = ["question_id"])]
//)
//
//data class QuestionOption(
//    @PrimaryKey(autoGenerate = true)
//    var id: Long = 0,
//
//    @ColumnInfo(name = "option_serverId")
//    var serverId: String? = null, // Nullable if not synced yet
//
//    @ColumnInfo(name = "question_id")
//    var questionId: Long = 0, // Foreign key referring to QuestionInfo
//
//    @ColumnInfo(name = "option_text")
//    var optionText: String
//)
//
//data class QuestionWithOptions(
//    @Embedded var question: QuestionInfo? = null,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "question_id"
//    )
//    var options: List<QuestionOption>? = null
//)
