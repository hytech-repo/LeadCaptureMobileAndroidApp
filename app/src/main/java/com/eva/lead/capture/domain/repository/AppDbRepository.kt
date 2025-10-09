package com.eva.lead.capture.domain.repository

import com.eva.lead.capture.domain.model.entity.DeviceInfo
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuickNote
import kotlinx.coroutines.flow.Flow

interface AppDbRepository {

    fun getAllExhibitors(): Flow<List<Exhibitor>>

    fun getExhibitorById(id: String): Flow<Exhibitor?>

    fun getExhibitorByLeadCode(leadCode: String): Flow<Exhibitor?>

    suspend fun insertExhibitor(user: Exhibitor): Long

    suspend fun updateExhibitor(user: Exhibitor)

    suspend fun insertLead(lead: EvaLeadData): Long
    suspend fun updateLead(lead: EvaLeadData): Int

    fun getLeadById(leadId: String): Flow<EvaLeadData?>

    fun getAllLeads(): Flow<List<EvaLeadData>?>

    suspend fun insertQuestionInfo(questionInfo: QuestionInfo): Long

    suspend fun updateQuestionInfo(questionInfo: QuestionInfo)

    suspend fun insertQuestionInfos(questionInfo: List<QuestionInfo>): List<Long>

    fun getQuestionsWithOptions(type: String): Flow<List<QuestionInfo>?>
    fun getActiveQuestions(type: String): Flow<List<QuestionInfo>?>

    suspend fun insertQuickNoteOption(note: QuickNote): Long

    suspend fun insertQuickNoteOptionList(note: List<QuickNote>): List<Long>

    suspend fun updateQuickNote(note: QuickNote): Int

    suspend fun deleteQuickNote(note: QuickNote): Int

    fun getQuickNoteList(): Flow<List<QuickNote>?>

    fun getActiveQuickNotes(): Flow<List<QuickNote>?>

    suspend fun insertMediaFile(media: LeadAudioRecording): Long

    fun getAllRecording(): Flow<List<LeadAudioRecording>>

    fun getRecordingById(id: String): Flow<LeadAudioRecording?>

    suspend fun insertDevice(device: DeviceInfo): Long

    fun getAllDevices(): Flow<List<DeviceInfo>?>

//    suspend fun deleteUser(user: User)

    suspend fun executeRawQuery(query: String): Long

}