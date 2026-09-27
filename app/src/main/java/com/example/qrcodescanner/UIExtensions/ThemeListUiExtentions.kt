package com.example.qrcodescanner.UIExtensions

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.ThemeList

// 1. Получаем ID строки для отображения в UI
@StringRes
fun ThemeList.getBtnId(): Int = when (this) {
    ThemeList.SystemDefault -> R.id.btnThemeSystem
    ThemeList.Light -> R.id.btnThemeLight
    ThemeList.Dark -> R.id.btnThemeDark
}



// 2. Получаем системную константу Android для переключения темы
fun ThemeList.getMode(): Int = when (this) {
    ThemeList.SystemDefault -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    ThemeList.Light -> AppCompatDelegate.MODE_NIGHT_NO
    ThemeList.Dark -> AppCompatDelegate.MODE_NIGHT_YES
}

// 3. Автоматически собираем все темы в список (как делали с языками)

fun ThemeList.Companion.fromMode(code: Int?): ThemeList {
    return ThemeList.getAll().find { it.getMode() == code } ?: ThemeList.SystemDefault
}

fun ThemeList.Companion.fromBtnId(code: Int?): ThemeList {
    return ThemeList.getAll().find { it.getBtnId() == code } ?: ThemeList.SystemDefault
}

fun ThemeList.Companion.getAll(): List<ThemeList> {
    return listOf(ThemeList.SystemDefault, ThemeList.Light, ThemeList.Dark)
}
