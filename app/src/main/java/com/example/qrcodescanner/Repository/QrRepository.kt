package com.example.qrcodescanner.Repository

import com.example.qrcodescanner.DataBase.Dao
import com.example.qrcodescanner.DataBase.Tables.ProfileTable
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import kotlinx.coroutines.flow.Flow

class QrRepository(private val dao: Dao) {

    // Сохранить QR
    suspend fun saveQrCode(qrItem: QrCodeItemTable): Long {
        return dao.addNewQrItem(qrItem)
    }

    fun getAllItem(): Flow<List<QrCodeItemTable>>{
        return dao.getAllQrItem()
    }
    fun getQrItemById(id: Long): Flow<QrCodeItemTable?> {
        return dao.getQrItemById(id)
    }

    fun getAllProfileSettings(): Flow<ProfileTable?>{
        return dao.getAllProfileSettings()
    }

    fun getThemeMode(): Int{
        return dao.getThemeMode()
    }

    suspend fun updateThemeMode(themeMode: Int){
        dao.updateThemeMode(themeMode)
    }
    suspend fun updateLanguage(languageCode: String){
        dao.updateLanguage(languageCode)
    }

    suspend fun updateFavoriteStatus(id: Long, favoriteStatus: Boolean){
        dao.updateFavoriteStatus(id, favoriteStatus)
    }

    suspend fun saveDefProfileSettings(settings: ProfileTable){
        dao.addDefaultSettings(settings)
    }



}