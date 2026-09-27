package com.example.qrcodescanner.Handler

import android.net.Uri
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.example.qrcodescanner.DataClass.QrCodeInfo
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.getText
import com.example.qrcodescanner.databinding.ItemContactTypeBinding
import com.example.qrcodescanner.databinding.ItemEmailTypeBinding
import com.example.qrcodescanner.databinding.ItemEventTypeBinding
import com.example.qrcodescanner.databinding.ItemGpsTypeBinding
import com.example.qrcodescanner.databinding.ItemMyqrTypeBinding
import com.example.qrcodescanner.databinding.ItemPhoneTypeBinding
import com.example.qrcodescanner.databinding.ItemSmsTypeBinding
import com.example.qrcodescanner.databinding.ItemTextTypeBinding
import com.example.qrcodescanner.databinding.ItemUrlTypeBinding
import com.example.qrcodescanner.databinding.ItemWifiTypeBinding

// 1. URL Handler (item_url_type.xml)
class UrlFormHandler(private val binding: ItemUrlTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val url = binding.etInput.text.toString().trim()
        if (url.isEmpty()) {
            binding.etInput.error = "Введите URL"
            return null
        }
        val rawValue = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        val type = QrItemCreation.URL.getText()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 2. Text Handler (item_text_type.xml)
class TextFormHandler(private val binding: ItemTextTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val text = binding.etInput.text.toString().trim()
        if (text.isEmpty()) {
            binding.etInput.error = "Введите текст"
            return null
        }
        val type = QrItemCreation.TEXT.getText()
        val qrItem = QrCodeInfo(type, text)
        return qrItem
    }
}

// 3. Wi-Fi Handler (item_wifi_type.xml)
class WifiFormHandler(private val binding: ItemWifiTypeBinding) : QrFormHandler {
    private var isPasswordVisible = false

    init {
        // Логика переключения видимости пароля
        binding.btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            binding.etWifiPassword.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.etWifiPassword.setSelection(binding.etWifiPassword.text.length)
        }
    }

    override fun validateAndBuildPayload(): QrCodeInfo? {
        val ssid = binding.etWifiSsid.text.toString().trim()
        val password = binding.etWifiPassword.text.toString().trim()
        val encryptionText = binding.spinnerEncryption.text.toString().trim()
        val isHidden = binding.switchIsHidden.isChecked

        if (ssid.isEmpty()) {
            binding.etWifiSsid.error = "Введите имя сети (SSID)"
            return null
        }

        val passType = when {
            encryptionText.contains("WPA", ignoreCase = true) -> "WPA"
            encryptionText.contains("WEP", ignoreCase = true) -> "WEP"
            else -> "nopass"
        }

        val rawValue = "WIFI:T:$passType;S:$ssid;P:$password;H:$isHidden;;"
        val type = QrItemCreation.WIFI.getText()
        val qrItem = QrCodeInfo(type, rawValue )

        return qrItem

    }
}

// 4. SMS Handler (item_sms_type.xml)
class SmsFormHandler(private val binding: ItemSmsTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val phone = binding.etSmsPhone.text.toString().trim()
        val message = binding.etSmsMessage.text.toString().trim()

        if (phone.isEmpty()) {
            binding.etSmsPhone.error = "Введите номер телефона"
            return null
        }

        val rawValue = "SMSTO:$phone:$message"
        val type = QrItemCreation.SMS.getText()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 5. Phone Handler (item_phone_type.xml)
class PhoneFormHandler(private val binding: ItemPhoneTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val phone = binding.etPhoneNumber.text.toString().trim()
        if (phone.isEmpty()) {
            binding.etPhoneNumber.error = "Введите номер телефона"
            return null
        }
        val rawValue = "TEL:$phone"
        val type = QrItemCreation.PHONE.getText()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 6. Email Handler (item_email_type.xml)
class EmailFormHandler(private val binding: ItemEmailTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val email = binding.etEmailAddress.text.toString().trim()
        val subject = binding.etEmailSubject.text.toString().trim()
        val body = binding.etEmailBody.text.toString().trim()

        if (email.isEmpty()) {
            binding.etEmailAddress.error = "Введите Email"
            return null
        }

        val rawValue = "MATMSG:TO:$email;SUB:$subject;BODY:$body;;"
        val type = QrItemCreation.EMAIL.getText()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 7. Contact / VCard Handler (item_contact_type.xml)
class ContactFormHandler(private val binding: ItemContactTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val fullName = binding.etFullName.text.toString().trim()
        val company = binding.etCompany.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (fullName.isEmpty() && phone.isEmpty()) {
            binding.etFullName.error = "Укажите имя или телефон"
            return null
        }

        val rawValue = buildString {
            append("BEGIN:VCARD\n")
            append("VERSION:3.0\n")
            if (fullName.isNotEmpty()) append("FN:$fullName\n")
            if (company.isNotEmpty()) append("ORG:$company\n")
            if (phone.isNotEmpty()) append("TEL:$phone\n")
            if (email.isNotEmpty()) append("EMAIL:$email\n")
            if (address.isNotEmpty()) append("ADR:;;$address;;;;\n")
            if (notes.isNotEmpty()) append("NOTE:$notes\n")
            append("END:VCARD")
        }
        val type = QrItemCreation.VCARD.getText()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 8. MyQR Handler (item_myqr_type.xml)
class MyQrFormHandler(private val binding: ItemMyqrTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val fullName = binding.etMyQrFullName.text.toString().trim()
        val company = binding.etMyQrCompany.text.toString().trim()
        val phone = binding.etMyQrPhone.text.toString().trim()
        val email = binding.etMyQrEmail.text.toString().trim()
        val address = binding.etMyQrAddress.text.toString().trim()
        val notes = binding.etMyQrNotes.text.toString().trim()

        if (fullName.isEmpty() && phone.isEmpty()) {
            binding.etMyQrFullName.error = "Укажите имя или телефон"
            return null
        }

        val rawValue = buildString {
            append("BEGIN:VCARD\n")
            append("VERSION:3.0\n")
            if (fullName.isNotEmpty()) append("FN:$fullName\n")
            if (company.isNotEmpty()) append("ORG:$company\n")
            if (phone.isNotEmpty()) append("TEL:$phone\n")
            if (email.isNotEmpty()) append("EMAIL:$email\n")
            if (address.isNotEmpty()) append("ADR:;;$address;;;;\n")
            if (notes.isNotEmpty()) append("NOTE:$notes\n")
            append("END:VCARD")
        }
        val type = QrItemCreation.MeCARD.getText()
        val qrItem = QrCodeInfo(type, rawValue)

        return qrItem
    }
}

// 9. GPS Handler (item_gps_type.xml)
class GpsFormHandler(private val binding: ItemGpsTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val lat = binding.etLatitude.text.toString().trim()
        val lng = binding.etLongitude.text.toString().trim()
        val query = binding.etGpsQuery.text.toString().trim()

        if (lat.isEmpty() || lng.isEmpty()) {
            if (lat.isEmpty()) binding.etLatitude.error = "Обязательно"
            if (lng.isEmpty()) binding.etLongitude.error = "Обязательно"
            return null
        }

        val rawValue = if (query.isNotEmpty()) {
            "geo:$lat,$lng?q=${Uri.encode(query)}"
        } else {
            "geo:$lat,$lng"
        }
        val type = QrItemCreation.LOCATION.getText()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 10. Event / VCalendar Handler (item_event_type.xml)
class EventFormHandler(private val binding: ItemEventTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val title = binding.etEventTitle.text.toString().trim()
        val startDate = binding.etStartDate.text.toString().trim()
        val endDate = binding.etEndDate.text.toString().trim()
        val location = binding.etEventLocation.text.toString().trim()
        val description = binding.etEventDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.etEventTitle.error = "Введите название события"
            return null
        }

        val rawValue = buildString {
            append("BEGIN:VEVENT\n")
            append("SUMMARY:$title\n")
            if (startDate.isNotEmpty()) append("DTSTART:$startDate\n")
            if (endDate.isNotEmpty()) append("DTEND:$endDate\n")
            if (location.isNotEmpty()) append("LOCATION:$location\n")
            if (description.isNotEmpty()) append("DESCRIPTION:$description\n")
            append("END:VEVENT")
        }
        val type = QrItemCreation.EVENT.getText()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}