package com.eva.lead.capture.domain.repository

import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuestionOption
import com.eva.lead.capture.domain.model.entity.QuestionWithOptions
import kotlinx.coroutines.flow.Flow

interface AppDbRepository {

    fun getAllExhibitors(): Flow<List<Exhibitor>>

    fun getExhibitorById(id: String): Flow<Exhibitor?>

    fun getExhibitorByLeadCode(leadCode: String): Flow<Exhibitor?>


    suspend fun insertExhibitor(user: Exhibitor): Long

    suspend fun updateExhibitor(user: Exhibitor)

    suspend fun insertLead(lead: EvaLeadData): Long

    fun getLeadById(leadId: String): Flow<EvaLeadData?>

    fun getAllLeads(): Flow<List<EvaLeadData>?>

    suspend fun insertQuestionInfo(questionInfo: QuestionInfo): Long

    suspend fun insertOptions(options: List<QuestionOption>): List<Long>

    suspend fun insertOption(option: QuestionOption): Long

    fun getQuestionsWithOptions(type: String): Flow<List<QuestionWithOptions>?>

    suspend fun insertMediaFile(media: LeadAudioRecording): Long

    fun getAllRecording(): Flow<List<LeadAudioRecording>>

//    suspend fun deleteUser(user: User)

    suspend fun executeRawQuery(query: String): Long

}