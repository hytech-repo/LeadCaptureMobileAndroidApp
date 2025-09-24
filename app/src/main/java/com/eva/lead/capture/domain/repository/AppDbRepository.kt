package com.eva.lead.capture.domain.repository

import com.eva.lead.capture.domain.model.entity.Exhibitor
import kotlinx.coroutines.flow.Flow

interface AppDbRepository {

    fun getAllExhibitors(): Flow<List<Exhibitor>>

    fun getExhibitorById(id: String): Flow<Exhibitor?>

    fun getExhibitorByLeadCode(leadCode: String): Flow<Exhibitor?>


    suspend fun insertExhibitor(user: Exhibitor): Long
    suspend fun updateExhibitor(user: Exhibitor)

//    suspend fun deleteUser(user: User)

    suspend fun executeRawQuery(query: String): Long

}