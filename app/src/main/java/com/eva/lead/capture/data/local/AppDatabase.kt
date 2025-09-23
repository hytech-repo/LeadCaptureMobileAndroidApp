package com.eva.lead.capture.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.eva.lead.capture.data.local.dao.AppDao
import com.eva.lead.capture.domain.model.entity.User


@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun appDao(): AppDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get the singleton instance of AppDatabase
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db" // Name of the DB file
                )
                    .fallbackToDestructiveMigration(false) // Auto-reset DB on version mismatch (optional)
//                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new column with default value
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `events` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `eventId` TEXT,
                `userId` INTEGER,
                `name` TEXT,
                `logo` TEXT,
                `startDate` TEXT,
                `endDate` TEXT,
                `timezone` TEXT,
                `venueTitle` TEXT,
                `street` TEXT,
                `city` TEXT,
                `state` TEXT,
                `registrantCount` INTEGER,
                `sessionTimeout` INTEGER,
                `checkinType` INTEGER,
                `checkinOptions` INTEGER,
                `colorCode` TEXT,
                `backgroundImage` TEXT,
                `thankyouPageMessageLine1` TEXT,
                `thankyouPageMessageLine2` TEXT,
                `thankyouPageButtonText` TEXT,
                `printerTimeout` INTEGER,
                `apiTimeoutTime` INTEGER,
                `createdAt` TEXT,
                `updatedAt` TEXT
            )
            """.trimIndent()
                )
            }
        }
    }
}