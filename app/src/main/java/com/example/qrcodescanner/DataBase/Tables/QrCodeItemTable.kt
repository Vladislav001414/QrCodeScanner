package com.example.qrcodescanner.DataBase.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "qrCodeItem")

data class QrCodeItemTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val qrType: String,
    val rawValue: String,
    val status: String,
    val favorite: Boolean,
    val dateAdded: Long,
)