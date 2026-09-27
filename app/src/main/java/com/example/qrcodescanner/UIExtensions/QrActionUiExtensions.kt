package com.example.qrcodescanner.UIExtensions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.QrAction


@DrawableRes
fun QrAction.getIcon(): Int = when (this) {
    QrAction.OpenBrowser -> R.drawable.captive_portal_24px
    QrAction.CallPhone -> R.drawable.baseline_call_24
    QrAction.SendSms -> R.drawable.outline_sms_24
    QrAction.SearchInGoogle -> R.drawable.captive_portal_24px

    QrAction.AddCalendar -> R.drawable.baseline_edit_calendar_24
    QrAction.AddContact -> R.drawable.baseline_person_outline_24
    QrAction.ConnectWifi -> R.drawable.outline_wifi_24
    QrAction.OpenMaps -> R.drawable.outline_fmd_good_24
    QrAction.SendEmail -> R.drawable.outline_attach_email_24
    QrAction.CopyData -> R.drawable.baseline_content_copy_24
    QrAction.ShareData -> R.drawable.baseline_share_24
}

@StringRes
fun QrAction.getText(): Int = when (this) {
    QrAction.OpenBrowser -> R.string.action_open_browser
    QrAction.CallPhone -> R.string.action_call_phone
    QrAction.SendSms -> R.string.action_send_sms
    QrAction.SearchInGoogle -> R.string.action_search_google
    QrAction.AddCalendar -> R.string.action_add_calendar
    QrAction.AddContact -> R.string.action_add_contact
    QrAction.ConnectWifi -> R.string.action_connect_wifi
    QrAction.OpenMaps -> R.string.action_open_maps
    QrAction.SendEmail -> R.string.action_send_email
    QrAction.CopyData -> R.string.action_copy
    QrAction.ShareData -> R.string.action_share
}

fun QrAction.getCopyText(): Int = when (this) {
    QrAction.CallPhone -> R.string.copy_phone_number
    QrAction.SendSms -> R.string.copy_phone_number
    QrAction.SearchInGoogle -> R.string.action_copy
    QrAction.AddCalendar -> R.string.copy_event_details
    QrAction.AddContact -> R.string.copy_contact_name
    QrAction.ConnectWifi -> R.string.copy_password
    QrAction.OpenMaps -> R.string.action_copy
    QrAction.SendEmail -> R.string.copy_email
    QrAction.CopyData -> R.string.action_copy
    QrAction.ShareData -> R.string.action_copy
    QrAction.OpenBrowser -> R.string.copy_link
}


