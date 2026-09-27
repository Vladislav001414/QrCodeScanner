package com.example.qrcodescanner.Application

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Database
import com.example.qrcodescanner.DataBase.MovieDateBase
import com.example.qrcodescanner.Repository.QrRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class QrCodeScannerApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        val database = MovieDateBase.getInstance(applicationContext)


        val savedTheme = runBlocking(Dispatchers.IO) {QrRepository(database.getDBDao()).getThemeMode()}

        val themeToApply = savedTheme ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        AppCompatDelegate.setDefaultNightMode(themeToApply)
    }
}