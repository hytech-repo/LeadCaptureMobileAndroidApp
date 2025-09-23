package com.eva.lead.capture.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.eva.lead.capture.data.local.AppDatabase
import com.eva.lead.capture.domain.model.entity.User
import com.eva.lead.capture.domain.repository.AppDbRepository
import kotlinx.coroutines.flow.Flow

class AppDbRepositoryImpl(appDatabase: AppDatabase) : AppDbRepository {

    private val dao = appDatabase.appDao()

    override fun getAllUsers(): Flow<List<User>> = dao.getAllUsers()

    override fun getUserById(id: String): Flow<User?> = dao.getUserById(id)

    override suspend fun insertUser(user: User) = dao.insertUser(user)

    override suspend fun updateUser(user: User) = dao.updateUser(user)

    override suspend fun executeRawQuery(query: String): Long {
        val rawQuery = SimpleSQLiteQuery(query)
        return dao.executeRawQuery(rawQuery)
    }

}