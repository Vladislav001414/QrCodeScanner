package com.example.qrcodescanner.DataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.qrcodescanner.DataBase.Tables.ProfileTable
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    suspend fun addNewQrItem(qrCodeItem: QrCodeItemTable): Long

    @Insert
    suspend fun addDefaultSettings(profileTable: ProfileTable)

    @Query("SELECT * FROM qrCodeItem ORDER BY dateAdded DESC")
    fun getAllQrItem(): Flow<List<QrCodeItemTable>>
    @Query("SELECT * FROM qrCodeItem WHERE id = :id LIMIT 1")
    fun getQrItemById(id: Long): Flow<QrCodeItemTable?>

    @Query("UPDATE qrCodeItem SET favorite = :currentFavorite WHERE id == :currentId")
    suspend fun updateFavoriteStatus(currentId: Long?, currentFavorite: Boolean)

    @Query("SELECT * FROM profile LIMIT 1")
    fun getAllProfileSettings(): Flow<ProfileTable?>

    @Query("SELECT themeMode FROM profile LIMIT 1")
    fun getThemeMode(): Int

    @Query("UPDATE profile SET themeMode = :currentThemeMode")
    suspend fun updateThemeMode(currentThemeMode: Int)

    @Query("UPDATE profile SET Language = :currentLanguage")
    suspend fun updateLanguage(currentLanguage: String)

}