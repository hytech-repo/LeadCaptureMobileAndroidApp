package com.eva.lead.capture.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.eva.lead.capture.domain.model.entity.Exhibitor
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

    @RawQuery
    suspend fun executeRawQuery(query: SupportSQLiteQuery): Long

}