package com.example.qrcodescanner.enumClass

import com.example.qrcodescanner.R

enum class QrCreatorType (val title: String, val iconRes: Int) {
    URL("URL", R.drawable.baseline_link_24),
    TEXT("TEXT", R.drawable.baseline_text_fields_24),
    EMAIL("EMAIL", R.drawable.outline_email_24),
    PHONE("PHONE", R.drawable.baseline_call_24),
    SMS("SMS", R.drawable.outline_sms_24),
    VCARD("VCARD", R.drawable.outline_assignment_ind_24),
    MECARD("MECARD", R.drawable.baseline_person_outline_24),
    LOCATION("LOCATION", R.drawable.outline_fmd_good_24),
    WIFI("WI-FI", R.drawable.outline_wifi_24),
    EVENT("EVENT", R.drawable.baseline_calendar_month_24);

    companion object {
        private val map = entries.associateBy(QrCreatorType::title)
        fun fromMlKitType(type: String): QrCreatorType = map[type] ?: TEXT
    }
}