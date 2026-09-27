package com.example.qrcodescanner.Repository

import com.example.qrcodescanner.DataBase.Dao
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable

class QrRepository(private val dao: Dao) {

    // Сохранить QR
    suspend fun saveQrCode(qrItem: QrCodeItemTable) {
        dao.addNewQrItem(qrItem)
    }

    // Получить QR по ID
    suspend fun getQrCodeById(id: Long) {

    }
}