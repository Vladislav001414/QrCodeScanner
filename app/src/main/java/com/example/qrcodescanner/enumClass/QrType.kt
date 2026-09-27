package com.example.qrcodescanner.enumClass

import androidx.annotation.StringRes
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.QrAction
import com.google.mlkit.vision.barcode.common.Barcode

enum class QrType(val mlKitType: Int, val keyToDB: String, @StringRes val stringResId: Int, val primaryAction: QrAction?, val secondaryAction: QrAction? = null) {
    URL(Barcode.TYPE_URL,"URL", R.string.qr_type_url, QrAction.OpenBrowser),
    WIFI(Barcode.TYPE_WIFI,"WIFI", R.string.qr_type_wifi, QrAction.ConnectWifi),
    CONTACT(Barcode.TYPE_CONTACT_INFO,"VCARD", R.string.qr_type_contact, QrAction.AddContact, QrAction.CallPhone),
    PHONE(Barcode.TYPE_PHONE,"PHONE", R.string.qr_type_phone, QrAction.CallPhone, QrAction.SendSms),
    EMAIL(Barcode.TYPE_EMAIL,"EMAIL", R.string.qr_type_email, QrAction.SendEmail),
    SMS(Barcode.TYPE_SMS,"SMS", R.string.qr_type_sms, QrAction.SendSms, QrAction.CallPhone),
    GEO(Barcode.TYPE_GEO,"LOCATION", R.string.qr_type_geo, QrAction.OpenMaps, QrAction.SearchInGoogle),
    CALENDAR(Barcode.TYPE_CALENDAR_EVENT,"EVENT", R.string.qr_type_calendar, QrAction.AddCalendar),
    DRIVER_LICENSE(Barcode.TYPE_DRIVER_LICENSE,"", R.string.qr_type_driver_license, QrAction.SearchInGoogle),
    PRODUCT(Barcode.TYPE_PRODUCT,"", R.string.qr_type_product, QrAction.SearchInGoogle),
    ISBN(Barcode.TYPE_ISBN,"", R.string.qr_type_isbn, QrAction.SearchInGoogle),
    TEXT(Barcode.TYPE_TEXT,"TEXT", R.string.qr_type_text, QrAction.SearchInGoogle),
    UNKNOWN(-1, "",R.string.qr_type_unknown, QrAction.SearchInGoogle);

    companion object {
        private val map = entries.associateBy(QrType::mlKitType)
        fun fromMlKitType(type: Int): QrType = map[type] ?: UNKNOWN
    }
}