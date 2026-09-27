package com.example.qrcodescanner.QrLogicalClass

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import java.util.Locale

object SettingsDefaults {


    fun getDefaultLanguageCode(): String {
        val appLocale = AppCompatDelegate.getApplicationLocales()[0]?.language
        return appLocale ?: Locale.getDefault().language
    }


    fun getDefaultThemeMode(): Int {
        val mode = AppCompatDelegate.getDefaultNightMode()
        return if (mode == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        } else {
            mode
        }
    }
}