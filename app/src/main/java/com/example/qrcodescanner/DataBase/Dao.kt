package com.example.qrcodescanner.DataBase

import androidx.room.Dao
import androidx.room.Insert
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable

@Dao
interface Dao {
    @Insert
    suspend fun addNewQrItem(qrCodeItem: QrCodeItemTable): Long
}