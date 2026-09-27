package com.example.qrcodescanner.SealedInterface

sealed interface LanguageList {
    companion object
    object Russian: LanguageList
    object English: LanguageList
    object Spanish: LanguageList
    object SimplifiedChina: LanguageList
    object German: LanguageList
    object French: LanguageList
    object Japanese: LanguageList
    object Polish: LanguageList
    object BrazilianPortuguese: LanguageList
    object Arabic: LanguageList
    object Korean: LanguageList
    object Ukrainian: LanguageList
    object Hindi: LanguageList
}