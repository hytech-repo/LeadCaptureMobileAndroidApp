package com.eva.lead.capture.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.eva.lead.capture.data.local.AppDatabase
import com.eva.lead.capture.domain.model.entity.EvaLeadData
import com.eva.lead.capture.domain.model.entity.Exhibitor
import com.eva.lead.capture.domain.model.entity.LeadAudioRecording
import com.eva.lead.capture.domain.model.entity.QuestionInfo
import com.eva.lead.capture.domain.model.entity.QuestionOption
import com.eva.lead.capture.domain.repository.AppDbRepository
import kotlinx.coroutines.flow.Flow

class AppDbRepositoryImpl(appDatabase: AppDatabase) : AppDbRepository {

    private val dao = appDatabase.appDao()

    override fun getAllExhibitors(): Flow<List<Exhibitor>> = dao.getAllExhibitors()

    override fun getExhibitorById(id: String): Flow<Exhibitor?> = dao.getExhibitorById(id)

    override fun getExhibitorByLeadCode(leadCode: String): Flow<Exhibitor?> =
        dao.getExhibitorByLeadCode(leadCode)

    override suspend fun insertExhibitor(exhibitor: Exhibitor) = dao.insertExhibitor(exhibitor)

    override suspend fun updateExhibitor(exhibitor: Exhibitor) = dao.updateExhibitor(exhibitor)

    override suspend fun insertLead(lead: EvaLeadData): Long = dao.insertLead(lead)

    override fun getLeadById(leadId: String): Flow<EvaLeadData?> = dao.getLeadById(leadId)

    override fun getAllLeads(): Flow<List<EvaLeadData>?> = dao.getAllLeadData()

    override suspend fun insertQuestionInfo(questionInfo: QuestionInfo): Long = dao.insertQuestionInfo(questionInfo)

    override suspend fun insertOptions(options: List<QuestionOption>): List<Long> = dao.insertOptions(options)

    override suspend fun insertOption(option: QuestionOption): Long = dao.insertOption(option)

    override suspend fun insertMediaFile(media: LeadAudioRecording): Long = dao.insertMediaFile(media)

    override fun getAllRecording(): Flow<List<LeadAudioRecording>> = dao.getAllRecording()

    override suspend fun executeRawQuery(query: String): Long {
        val rawQuery = SimpleSQLiteQuery(query)
        return dao.executeRawQuery(rawQuery)
    }

}