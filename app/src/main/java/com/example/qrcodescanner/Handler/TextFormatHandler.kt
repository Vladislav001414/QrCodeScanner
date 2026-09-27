package com.example.qrcodescanner.Handler

import android.content.Context
import com.example.qrcodescanner.R

object TextFormatHandler {

    fun formatRawPayload(context: Context, rawPayload: String): String {
        val trimmed = rawPayload.trim()

        return when {
            trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) -> parseVCard(context, trimmed)
            trimmed.startsWith("WIFI:", ignoreCase = true) -> parseWifi(context, trimmed)
            trimmed.startsWith("SMSTO:", ignoreCase = true) -> parseSms(context, trimmed)
            trimmed.startsWith("MATMSG:", ignoreCase = true) || trimmed.startsWith("mailto:", ignoreCase = true) -> parseEmail(context, trimmed)
            trimmed.startsWith("BEGIN:VEVENT", ignoreCase = true) -> parseEvent(context, trimmed)
            trimmed.startsWith("geo:", ignoreCase = true) -> parseGps(context, trimmed)
            else -> trimmed
        }
    }

    private fun parseVCard(context: Context, raw: String): String {
        var name = ""
        var company = ""
        var phone = ""
        var email = ""
        var address = ""
        var note = ""

        raw.lines().forEach { line ->
            when {
                line.startsWith("FN:", true) -> name = line.substring(3).trim()
                line.startsWith("ORG:", true) -> company = line.substring(4).trim()
                line.startsWith("TEL:", true) -> phone = line.substring(4).trim()
                line.startsWith("EMAIL:", true) -> email = line.substring(6).trim()
                line.startsWith("ADR:", true) -> address = line.substring(4).replace(";", " ").trim()
                line.startsWith("NOTE:", true) -> note = line.substring(5).trim()
            }
        }

        return buildString {
            append(context.getString(R.string.contact)).append(":\n")
            if (name.isNotEmpty()) append("${context.getString(R.string.name)}: $name\n")
            if (company.isNotEmpty()) append("Компания: $company\n")
            if (phone.isNotEmpty()) append("${context.getString(R.string.contact_phone)}: $phone\n")
            if (email.isNotEmpty()) append("${context.getString(R.string.email)}: $email\n")
            if (address.isNotEmpty()) append("Адрес: $address\n")
            if (note.isNotEmpty()) append("Заметка: $note")
        }.trimEnd()
    }

    private fun parseWifi(context: Context, raw: String): String {
        val ssid = Regex("S:([^;]+)", RegexOption.IGNORE_CASE).find(raw)?.groupValues?.get(1) ?: ""
        val password = Regex("P:([^;]+)", RegexOption.IGNORE_CASE).find(raw)?.groupValues?.get(1) ?: context.getString(R.string.no_password)

        return "${context.getString(R.string.wi_fi_network)}:\n" +
                "${context.getString(R.string.name)}: $ssid\n" +
                "${context.getString(R.string.password)}: $password"
    }

    private fun parseSms(context: Context, raw: String): String {
        val parts = raw.removePrefix("SMSTO:").split(":", limit = 2)
        val phone = parts.getOrNull(0) ?: ""
        val msg = parts.getOrNull(1) ?: ""

        return "${context.getString(R.string.sms_message)}:\n" +
                "${context.getString(R.string.to)}: $phone\n" +
                "${context.getString(R.string.text)}: $msg"
    }

    private fun parseEmail(context: Context, raw: String): String {
        val to = Regex("TO:([^;]+)", RegexOption.IGNORE_CASE).find(raw)?.groupValues?.get(1) ?: ""
        val sub = Regex("SUB:([^;]+)", RegexOption.IGNORE_CASE).find(raw)?.groupValues?.get(1) ?: ""
        val body = Regex("BODY:([^;]+)", RegexOption.IGNORE_CASE).find(raw)?.groupValues?.get(1) ?: ""

        return "${context.getString(R.string.email)}:\n" +
                "${context.getString(R.string.to)}: $to\n" +
                "${context.getString(R.string.subject)}: $sub\n" +
                "${context.getString(R.string.text)}: $body"
    }

    private fun parseEvent(context: Context, raw: String): String {
        val summary = Regex("SUMMARY:(.*)", RegexOption.IGNORE_CASE).find(raw)?.groupValues?.get(1)?.trim() ?: ""
        val location = Regex("LOCATION:(.*)", RegexOption.IGNORE_CASE).find(raw)?.groupValues?.get(1)?.trim() ?: ""

        return buildString {
            append(context.getString(R.string.event)).append(":\n")
            if (summary.isNotEmpty()) append("${context.getString(R.string.calendar_summary)}: $summary\n")
            if (location.isNotEmpty()) append("${context.getString(R.string.calendar_location)}: $location")
        }.trimEnd()
    }

    private fun parseGps(context: Context, raw: String): String {
        val clean = raw.removePrefix("geo:")
        return "Геолокация: $clean"
    }
}