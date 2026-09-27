package com.example.qrcodescanner.SealedInterface

sealed interface ThemeList {
    companion object
    object Dark: ThemeList
    object Light: ThemeList
    object SystemDefault: ThemeList
}