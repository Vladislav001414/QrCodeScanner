package com.example.qrcodescanner.DataBase.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class ProfileTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0,
    val language: String?,
    val themeMode: Int?,
)