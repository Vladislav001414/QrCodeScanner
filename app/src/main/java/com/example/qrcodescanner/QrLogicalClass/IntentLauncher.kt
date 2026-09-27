package com.example.qrcodescanner.QrLogicalClass

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.qrcodescanner.DataClass.QrCodeResult
import com.example.qrcodescanner.R
import com.example.qrcodescanner.SealedInterface.QrAction
import com.google.mlkit.vision.barcode.common.Barcode
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IntentLauncher(val context: Context){

    fun launchAction(action: QrAction, result: QrCodeResult){
        val intent = when (action){
            QrAction.OpenBrowser -> createBrowserIntent(result.displayValue)
            QrAction.SearchInGoogle -> createGoogleSearchIntent(result.displayValue)
            QrAction.ConnectWifi -> {
                connectToWifi(result.rawBarcode.wifi)
                null
            }
            QrAction.CallPhone -> createCallIntent(result.rawBarcode)
            QrAction.SendSms -> createSmsIntent(result.rawBarcode)
            QrAction.AddCalendar -> createCalendarIntent(result.rawBarcode.calendarEvent)
            QrAction.OpenMaps -> createMapsIntent(result.displayValue)
            QrAction.SendEmail -> createEmailIntent(result.rawBarcode.email)
            QrAction.AddContact -> createContactIntent(result.rawBarcode.contactInfo)
            QrAction.CopyData -> {
                copyToClipBoard(result.rawBarcode)
                null
            }
            QrAction.ShareData -> {
                shareResult(result.rawBarcode)
                null
            }
            else -> null
        }

        if (intent != null) {
            try {

                context.startActivity(intent as Intent?)
            } catch (e: ActivityNotFoundException) {

                fallbackOrShowError(action, result)
            }
        }
    }


    private fun createBrowserIntent(url: String): Intent {

        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else url
        return Intent(Intent.ACTION_VIEW, formattedUrl.toUri())
    }

    private fun createCallIntent(barcode: Barcode): Intent? {
        val phoneNumber = if(barcode.valueType == Barcode.TYPE_SMS) {
            barcode.sms?.phoneNumber
        } else {
            barcode.phone?.number
        }
        if (phoneNumber == null) return null

        return Intent(Intent.ACTION_DIAL, "tel:${phoneNumber}".toUri())
    }


    private fun createSmsIntent(barcode: Barcode): Intent? {


        val phoneNumber = if(barcode.valueType == Barcode.TYPE_SMS){
            barcode.sms?.phoneNumber
        } else {
            barcode.phone?.number
        }
        val message = if(barcode.valueType == Barcode.TYPE_SMS){
            barcode.sms?.message ?: ""
        } else {
            ""
        }
        if (phoneNumber == null) return null
        return Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:${phoneNumber}".toUri()


            putExtra("sms_body", message)
        }
    }

    private fun createGoogleSearchIntent(query: String): Intent {
        return Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, query)
        }
    }

    private fun createMapsIntent(coordinates: String): Intent {

        return Intent(Intent.ACTION_VIEW, "geo:0,0?q=${Uri.encode(coordinates)}".toUri())
    }

    private fun fallbackOrShowError(action: QrAction, qrResult: QrCodeResult) {
        Toast.makeText(context, "R.string.error_no_app_available", Toast.LENGTH_SHORT).show()
    }

    private fun createEmailIntent(email: Barcode.Email?): Intent? {
        if (email == null) return null
        return Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.address))
            putExtra(Intent.EXTRA_SUBJECT, email.subject ?: "")
            putExtra(Intent.EXTRA_TEXT, email.body ?: "")
        }
    }

    private fun createContactIntent(contact: Barcode.ContactInfo?): Intent? {
        if (contact == null) return null
        return Intent(Intent.ACTION_INSERT).apply {
            type = ContactsContract.Contacts.CONTENT_TYPE

            putExtra(ContactsContract.Intents.Insert.NAME, contact.name?.formattedName ?: "")

            putExtra(ContactsContract.Intents.Insert.PHONE, contact.phones.firstOrNull()?.number ?: "")

            putExtra(ContactsContract.Intents.Insert.EMAIL, contact.emails.firstOrNull()?.address ?: "")
        }
    }
    private fun createCalendarIntent(calendar: Barcode.CalendarEvent?): Intent? {
        if (calendar == null) return null
        return Intent(Intent.ACTION_INSERT).apply {
            val startTime = convertToMillis(calendar.start) ?: System.currentTimeMillis()
            val endTime = convertToMillis(calendar.end) ?: (startTime + 60L * 60L * 1000L)
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, calendar.summary?: "")
            putExtra(CalendarContract.Events.DESCRIPTION, calendar.description ?: "")
            putExtra(CalendarContract.Events.EVENT_LOCATION, calendar.location ?: "")
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            putExtra(CalendarContract.Events.STATUS, calendar.status?: "")
        }
    }
    private fun connectToWifi(wifi: Barcode.WiFi?) {
        if (wifi == null) return

        try {

            val suggestionBuilder = WifiNetworkSuggestion.Builder()
                .setSsid(wifi.ssid ?: "")


            when (wifi.encryptionType) {
                Barcode.WiFi.TYPE_WPA -> suggestionBuilder.setWpa2Passphrase(wifi.password ?: "")
                Barcode.WiFi.TYPE_WEP -> suggestionBuilder.setWpa3Passphrase(wifi.password ?: "")
                Barcode.WiFi.TYPE_OPEN -> { /* Open Network */ }
            }


            val list = arrayListOf(suggestionBuilder.build())
            val intent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS).apply {
                putParcelableArrayListExtra("android.provider.extra.WIFI_NETWORK_LIST", list)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)

        } catch (e: Exception) {

            context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }
    }
    private fun convertToMillis(dateTime: Barcode.CalendarDateTime?): Long? {
        if (dateTime == null) return null


        val calendar = Calendar.getInstance()


        calendar.set(
            dateTime.year,
            dateTime.month - 1,
            dateTime.day,
            if (dateTime.hours != -1) dateTime.hours else 0,
            if (dateTime.minutes != -1) dateTime.minutes else 0,
            if (dateTime.seconds != -1) dateTime.seconds else 0
        )

        return calendar.timeInMillis
    }

    private fun copyToClipBoard(barcode: Barcode){
        Log.d("text", "copyToClipBoard")
        val textToCopy = when (barcode.valueType) {
            Barcode.TYPE_WIFI -> barcode.wifi?.password ?: ""
            Barcode.TYPE_URL -> barcode.url?.url ?: ""
            Barcode.TYPE_PHONE -> barcode.phone?.number ?: ""
            Barcode.TYPE_SMS,
            Barcode.TYPE_EMAIL,
            Barcode.TYPE_CALENDAR_EVENT,
            Barcode.TYPE_CONTACT_INFO -> getCleanTextForSharingAndCopying(barcode)

            else -> barcode.displayValue ?: ""
        }

        if (textToCopy.isEmpty()) {
            Toast.makeText(context, context.getString(R.string.nothing_to_copy), Toast.LENGTH_SHORT).show()
            return
        }


        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        val clip = ClipData.newPlainText("QR_Code_Data", textToCopy)


        clipboard.setPrimaryClip(clip)


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(context, context.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
        }
    }
    fun shareResult(barcode: Barcode) {

        when (barcode.valueType) {

            Barcode.TYPE_CONTACT_INFO -> {
                val vCardText = createVCardContent(barcode.contactInfo)
                if (vCardText != null) {
                    shareAsFile(vCardText, "${barcode.contactInfo?.name ?: "contact"}.vcf", "text/vcard")
                    return // Выходим, так как поделились файлом
                }
            }


            Barcode.TYPE_CALENDAR_EVENT -> {
                val iCalText = createICalendarContent(barcode.calendarEvent)
                if (iCalText != null) {
                    shareAsFile(iCalText, "${barcode.calendarEvent?.summary ?: "event"}.ics", "text/calendar")
                    return
                }
            }
        }


        val shareText = getCleanTextForSharingAndCopying(barcode)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_via)))
    }


    private fun shareAsFile(fileContent: String, fileName: String, mimeType: String) {
        try {

            val cacheDir = context.cacheDir
            val sharedFile = File(cacheDir, fileName)


            val writer = FileWriter(sharedFile)
            writer.write(fileContent)
            writer.close()


            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                sharedFile
            )


            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, contentUri)

                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_file))
            context.startActivity(chooserIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.file_creation_error), Toast.LENGTH_SHORT).show()
        }
    }


    private fun createVCardContent(contact: Barcode.ContactInfo?): String? {
        if (contact == null) return null
        val name = contact.name?.formattedName ?: context.getString(R.string.record_from_qr)
        val phone = contact.phones.firstOrNull()?.number ?: ""
        val email = contact.emails.firstOrNull()?.address ?: ""

        return """
        BEGIN:VCARD
        VERSION:3.0
        FN:$name
        TEL;TYPE=CELL:$phone
        EMAIL;TYPE=PREF,INTERNET:$email
        END:VCARD
    """.trimIndent()
    }


    private fun createICalendarContent(calendarEvent: Barcode.CalendarEvent?): String? {
        if (calendarEvent == null) return null
        val summary = calendarEvent.summary ?: context.getString(R.string.event)
        val description = calendarEvent.description ?: ""
        val location = calendarEvent.location ?: ""


        val startStr = calendarEvent.start?.rawValue ?: ""
        val endStr = calendarEvent.end?.rawValue ?: ""

        return """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:-//QR Scanner App//NONSGML v1.0//EN
        BEGIN:VEVENT
        SUMMARY:$summary
        DESCRIPTION:$description
        LOCATION:$location
        DTSTART:$startStr
        DTEND:$endStr
        END:VEVENT
        END:VCALENDAR
    """.trimIndent()
    }

    private fun getCleanTextForSharingAndCopying(barcode: Barcode): String {


        return when (barcode.valueType) {

            Barcode.TYPE_WIFI -> {
                val ssid = barcode.wifi?.ssid ?: ""
                val password = barcode.wifi?.password ?: context.getString(R.string.no_password)
                "${context.getString(R.string.wi_fi_network)}:\n${context.getString(R.string.name)}: $ssid\n${context.getString(
                    R.string.password)}: $password"
            }

            Barcode.TYPE_SMS -> {
                val sms = barcode.sms
                val phone = sms?.phoneNumber ?: ""
                val msg = sms?.message ?: ""
                "${context.getString(R.string.sms_message)}:\n${context.getString(R.string.to)}: $phone\n${context.getString(
                    R.string.text)}: $msg"
            }

            Barcode.TYPE_EMAIL -> {
                val email = barcode.email
                val to = email?.address ?: ""
                val subject = email?.subject ?: ""
                val body = email?.body ?: ""
                "${context.getString(R.string.email)}:\n${context.getString(R.string.to)}: $to\n${context.getString(
                    R.string.subject)}: $subject\n${context.getString(R.string.text)}: $body"
            }

            Barcode.TYPE_CONTACT_INFO -> {
                val contact = barcode.contactInfo
                val name = contact?.name?.formattedName ?: ""
                val phone = contact?.phones?.firstOrNull()?.number ?: ""
                val email = contact?.emails?.firstOrNull()?.address ?: ""
                "${context.getString(R.string.contact)}:\n${context.getString(R.string.name)}: $name\n${context.getString(
                    R.string.contact_phone)}: $phone\n${context.getString(R.string.email)}: $email"
            }
            Barcode.TYPE_CALENDAR_EVENT -> {
                val event = barcode.calendarEvent
                val summary = event?.summary ?: ""
                val description = event?.description ?: ""
                val location = event?.location ?: ""
                val start = formatCalendarDateTime(event?.start)
                val end = formatCalendarDateTime(event?.end)
                "${context.getString(R.string.event)}:\n" +
                "${context.getString(R.string.calendar_summary)}: $summary\n" +
                "${context.getString(R.string.calendar_description)}: $description\n" +
                "${context.getString(R.string.calendar_location)}: $location\n" +
                "${context.getString(R.string.calendar_start)}: $start\n" +
                "${context.getString(R.string.calendar_end)}: $end }"
            }


            else -> barcode.displayValue ?: ""
        }
    }

    private fun formatCalendarDateTime(dateTime: Barcode.CalendarDateTime?): String {
        if (dateTime == null) return ""

        val calendar = Calendar.getInstance()


        calendar.set(
            dateTime.year,
            dateTime.month - 1,
            dateTime.day,
            if (dateTime.hours != -1) dateTime.hours else 0,
            if (dateTime.minutes != -1) dateTime.minutes else 0,
            if (dateTime.seconds != -1) dateTime.seconds else 0
        )


        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return formatter.format(calendar.time)
    }


}