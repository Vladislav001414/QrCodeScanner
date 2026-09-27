package com.example.qrcodescanner.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.qrcodescanner.DataBase.Tables.ProfileTable
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable

@Database(entities = [QrCodeItemTable::class, ProfileTable::class], version = 1, exportSchema = false)
abstract class MovieDateBase : RoomDatabase() {
    abstract fun getDBDao(): Dao

    companion object {
        @Volatile private var INSTANCE: MovieDateBase? = null

        fun getInstance(context: Context): MovieDateBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MovieDateBase::class.java,
                    "database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}