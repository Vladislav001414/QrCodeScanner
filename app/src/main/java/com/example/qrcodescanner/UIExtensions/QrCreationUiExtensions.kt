package com.example.qrcodescanner.UIExtensions

import android.view.View
import android.widget.ViewFlipper
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.QrItemCreation

@DrawableRes
fun QrItemCreation.getIcon(): Int = when (this){
    QrItemCreation.URL -> R.drawable.baseline_link_24
    QrItemCreation.TEXT -> R.drawable.baseline_text_fields_24
    QrItemCreation.EMAIL -> R.drawable.outline_email_24
    QrItemCreation.PHONE -> R.drawable.baseline_call_24
    QrItemCreation.SMS -> R.drawable.outline_sms_24
    QrItemCreation.VCARD -> R.drawable.outline_assignment_ind_24
    QrItemCreation.MeCARD -> R.drawable.baseline_person_outline_24
    QrItemCreation.LOCATION -> R.drawable.outline_fmd_good_24
    QrItemCreation.WIFI -> R.drawable.outline_wifi_24
    QrItemCreation.EVENT -> R.drawable.baseline_calendar_month_24
    QrItemCreation.PRODUCT -> R.drawable.baseline_shopping_basket_24
}

@StringRes
fun QrItemCreation.getText(): Int = when (this){
    QrItemCreation.URL -> R.string.qr_type_url
    QrItemCreation.TEXT -> R.string.qr_type_text
    QrItemCreation.EMAIL -> R.string.qr_type_email
    QrItemCreation.PHONE -> R.string.qr_type_phone
    QrItemCreation.SMS -> R.string.qr_type_sms
    QrItemCreation.VCARD -> R.string.qr_type_vcard_title
    QrItemCreation.MeCARD -> R.string.qr_type_mecard_title
    QrItemCreation.LOCATION -> R.string.qr_type_geo
    QrItemCreation.WIFI -> R.string.qr_type_wifi
    QrItemCreation.EVENT -> R.string.qr_type_calendar
    QrItemCreation.PRODUCT -> R.string.qr_type_product
}

@IdRes
fun QrItemCreation.getFormId(): Int = when (this) {
    QrItemCreation.URL -> R.id.formUrl
    QrItemCreation.TEXT -> R.id.formText
    QrItemCreation.WIFI -> R.id.formWifi
    QrItemCreation.SMS -> R.id.formSms
    QrItemCreation.PHONE -> R.id.formPhone
    QrItemCreation.EMAIL -> R.id.formEmail
    QrItemCreation.VCARD -> R.id.formVCard
    QrItemCreation.MeCARD -> R.id.formMeCard
    QrItemCreation.LOCATION -> R.id.formLocation
    QrItemCreation.EVENT -> R.id.formEvent
    QrItemCreation.PRODUCT -> TODO()
}

fun QrItemCreation.toDbKey(): String = when (this) {
    QrItemCreation.URL -> "URL"
    QrItemCreation.TEXT -> "TEXT"
    QrItemCreation.EMAIL -> "EMAIL"
    QrItemCreation.PHONE -> "PHONE"
    QrItemCreation.SMS -> "SMS"
    QrItemCreation.VCARD -> "VCARD"
    QrItemCreation.MeCARD -> "MeCARD"
    QrItemCreation.LOCATION -> "LOCATION"
    QrItemCreation.WIFI -> "WIFI"
    QrItemCreation.EVENT -> "EVENT"
    QrItemCreation.PRODUCT -> "PRODUCT"
}

fun QrItemCreation.Companion.fromString(type: String?): QrItemCreation = when (type) {
    "URL" -> QrItemCreation.URL
    "TEXT" -> QrItemCreation.TEXT
    "EMAIL" -> QrItemCreation.EMAIL
    "PHONE" -> QrItemCreation.PHONE
    "SMS" -> QrItemCreation.SMS
    "VCARD" -> QrItemCreation.VCARD
    "MeCARD" -> QrItemCreation.MeCARD
    "LOCATION" -> QrItemCreation.LOCATION
    "WIFI" -> QrItemCreation.WIFI
    "EVENT" -> QrItemCreation.EVENT
    "PRODUCT" -> QrItemCreation.PRODUCT
    else -> QrItemCreation.TEXT
}

fun ViewFlipper.showViewById(@IdRes viewId: Int): View? {
    val targetView = findViewById<View>(viewId) ?: return null
    val index = indexOfChild(targetView)
    if (index != -1) {
        displayedChild = index
    }
    return targetView
}



fun QrItemCreation.Companion.getAllItems(): List<QrItemCreation> = listOf(
    QrItemCreation.URL,
    QrItemCreation.TEXT,
    QrItemCreation.EMAIL,
    QrItemCreation.PHONE,
    QrItemCreation.SMS,
    QrItemCreation.VCARD,
    QrItemCreation.MeCARD,
    QrItemCreation.LOCATION,
    QrItemCreation.WIFI,
    QrItemCreation.EVENT
)