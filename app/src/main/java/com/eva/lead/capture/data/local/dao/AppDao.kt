package com.eva.lead.capture.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExhibitor(user: Exhibitor): Long

    @Update
    suspend fun updateExhibitor(user: Exhibitor)

    @Query("SELECT * FROM exhibitor ORDER BY id DESC")
    fun getAllExhibitors(): Flow<List<Exhibitor>>

    @Query("SELECT * FROM exhibitor WHERE user_id = :userId")
    fun getExhibitorById(userId: String): Flow<Exhibitor?>

    @Query("SELECT * FROM exhibitor WHERE lead_code = :leadCode")
    fun getExhibitorByLeadCode(leadCode: String): Flow<Exhibitor?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLead(leadData: EvaLeadData): Long

    @Query("SELECT * FROM lead_data ORDER BY id DESC")
    fun getAllLeadData(): Flow<List<EvaLeadData>?>

    @Query("SELECT * FROM lead_data where lead_id = :leadId")
    fun getLeadById(leadId: String): Flow<EvaLeadData?>

    @Update
    suspend fun updateQuestionInfo(questionInfo: QuestionInfo)

    @Insert
    suspend fun insertQuestionInfo(questionInfo: QuestionInfo): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuestionInfos(questionInfo: List<QuestionInfo>): List<Long>

    @Query("SELECT * FROM question_info WHERE type=:type AND is_deleted = 0")
    fun getQuestionsWithOptions(type: String): Flow<List<QuestionInfo>?>

    @Insert
    suspend fun insertMediaFile(media: LeadAudioRecording): Long

    @Query("SELECT * FROM audio_recording where type = 'recording' ORDER BY id DESC")
    fun getAllRecording(): Flow<List<LeadAudioRecording>>

    @RawQuery
    suspend fun executeRawQuery(query: SupportSQLiteQuery): Long

}