package com.eva.lead.capture.domain.worker

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.eva.lead.capture.data.remote.RetrofitClient
import com.eva.lead.capture.data.local.AppDatabase
import com.eva.lead.capture.data.repository.AuthRepositoryImpl
import com.eva.lead.capture.domain.model.LoginRequest
import com.eva.lead.capture.utils.ResultWrapper
import kotlin.collections.isNullOrEmpty
import kotlin.text.isNullOrEmpty
import kotlin.to

class AttendeesSyncWorker(
    private val mContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(mContext, workerParams){

    val authRepo = AuthRepositoryImpl(RetrofitClient.apiService)
    val appDatabase = AppDatabase.getInstance(mContext)

    override suspend fun doWork(): Result {
        val eventId = inputData.getString("event_id")
        if (!eventId.isNullOrEmpty()) {
            setProgress(workDataOf("status" to "in_progress"))
            val request = LoginRequest(eventId, "password")
            val registrantList = authRepo.login(request)
            if (registrantList is ResultWrapper.Success) {
                val data = registrantList.data

                // Perform db operation in background...

                setProgress(workDataOf("status" to "data saved"))
            } else if (registrantList is ResultWrapper.Error) {
                setProgress(workDataOf("status" to "failed"))
                return Result.failure(workDataOf("result" to "failed"))
            }
        }
        return Result.success(workDataOf("result" to "completed"))
    }
}