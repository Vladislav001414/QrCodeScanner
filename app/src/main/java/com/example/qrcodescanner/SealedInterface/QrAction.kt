package com.example.qrcodescanner.SealedInterface

sealed interface QrAction {
    object OpenBrowser : QrAction
    object SearchInGoogle : QrAction
    object ConnectWifi : QrAction
    object AddContact : QrAction
    object CallPhone : QrAction
    object SendEmail : QrAction
    object SendSms : QrAction
    object OpenMaps : QrAction
    object AddCalendar : QrAction
    object CopyData: QrAction
    object ShareData: QrAction
}