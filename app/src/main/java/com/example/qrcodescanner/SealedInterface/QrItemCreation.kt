package com.example.qrcodescanner.SealedInterface

sealed interface QrItemCreation {

    data object URL: QrItemCreation
    data object TEXT: QrItemCreation
    data object EMAIL: QrItemCreation
    data object PHONE: QrItemCreation
    data object SMS: QrItemCreation
    data object VCARD: QrItemCreation
    data object MeCARD: QrItemCreation
    data object LOCATION: QrItemCreation
    data object WIFI: QrItemCreation
    data object EVENT: QrItemCreation

    companion object
}