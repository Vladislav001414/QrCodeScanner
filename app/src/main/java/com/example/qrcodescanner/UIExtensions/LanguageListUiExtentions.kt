package com.example.qrcodescanner.UIExtensions

import androidx.annotation.StringRes
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.LanguageList
import java.util.Locale

@StringRes
fun LanguageList.getText(): Int = when (this) {
    LanguageList.English -> R.string.lang_english
    LanguageList.Russian -> R.string.lang_russian
    LanguageList.Spanish -> R.string.lang_spanish
    LanguageList.SimplifiedChina -> R.string.lang_chinese
    LanguageList.German -> R.string.lang_german
    LanguageList.French -> R.string.lang_french
    LanguageList.Japanese -> R.string.lang_japanese
    LanguageList.Polish -> R.string.lang_polish
    LanguageList.BrazilianPortuguese -> R.string.lang_portuguese
    LanguageList.Arabic -> R.string.lang_arabic
    LanguageList.Korean -> R.string.lang_korean
    LanguageList.Ukrainian -> R.string.lang_ukrainian
    LanguageList.Hindi -> R.string.lang_hindi
}

fun LanguageList.getIsoCode(): String = when (this) {
    LanguageList.English -> "en"
    LanguageList.Russian -> "ru"
    LanguageList.Spanish -> "es"
    LanguageList.SimplifiedChina -> "zh"
    LanguageList.German -> "de"
    LanguageList.French -> "fr"
    LanguageList.Japanese -> "ja"
    LanguageList.Polish -> "pl"
    LanguageList.BrazilianPortuguese -> "pt" // Или "pt-BR" для конкретного региона
    LanguageList.Arabic -> "ar"
    LanguageList.Korean -> "ko"
    LanguageList.Ukrainian -> "uk"
    LanguageList.Hindi -> "hi"
}

// Ищет объект языка по его строковому коду. Если код не найден, возвращает English по умолчанию

fun LanguageList.Companion.fromIsoCode(code: String?): LanguageList {
    // 1. Если код передан (из БД), ищем его в нашем списке
    if (code != null) {
        val found = LanguageList.getAll().find { it.getIsoCode() == code }
        if (found != null) return found
    }

    // 2. Если в БД пусто (первый запуск), берем язык системы смартфона
    val systemLanguageCode = Locale.getDefault().language // вернет "ru", "uk", "en" и т.д.

    // 3. Ищем, поддерживает ли наше приложение этот системный язык
    return LanguageList.getAll().find { it.getIsoCode() == systemLanguageCode }
        ?: LanguageList.English // 4. Если системный язык не поддерживается, возвращаем Английский
}




// Для языков
fun LanguageList.Companion.getAll(): List<LanguageList> {
    return listOf(
        LanguageList.English, LanguageList.Russian, LanguageList.Spanish,
        LanguageList.SimplifiedChina, LanguageList.German, LanguageList.French,
        LanguageList.Japanese, LanguageList.Polish, LanguageList.BrazilianPortuguese, LanguageList.Arabic,
        LanguageList.Korean, LanguageList.Ukrainian, LanguageList.Hindi
    )
}

