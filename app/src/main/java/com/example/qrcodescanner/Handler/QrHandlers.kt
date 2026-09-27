package com.example.qrcodescanner.Handler

import android.net.Uri
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import com.example.qrcodescanner.DataClass.QrCodeInfo
import com.example.qrcodescanner.R
import com.example.qrcodescanner.RVAdapter.QrCreationAdapter
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import com.example.qrcodescanner.UIExtensions.getText
import com.example.qrcodescanner.UIExtensions.toDbKey
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
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// 1. URL Handler (item_url_type.xml)
class UrlFormHandler(private val binding: ItemUrlTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val url = binding.etInput.text.toString().trim()
        if (url.isEmpty()) {
            binding.etInput.error = binding.root.context.getString(R.string.enter_url)
            return null
        }
        val rawValue = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        val type = QrItemCreation.URL.toDbKey()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 2. Text Handler (item_text_type.xml)
class TextFormHandler(private val binding: ItemTextTypeBinding) : QrFormHandler {
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val text = binding.etInput.text.toString().trim()
        if (text.isEmpty()) {
            binding.etInput.error = binding.root.context.getString(R.string.enter_your_text)
            return null
        }
        val type = QrItemCreation.TEXT.toDbKey()
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
            binding.btnTogglePassword.setImageResource(
                if (isPasswordVisible)
                    R.drawable.outline_visibility_24
                else
                    R.drawable.outline_visibility_off_24
            )
            binding.etWifiPassword.setSelection(binding.etWifiPassword.text.length)
        }


        val encryptionTypes = binding.root.resources.getStringArray(R.array.wifi_encryption_types)

        binding.spinnerEncryption.setSimpleItems(encryptionTypes)

        binding.spinnerEncryption.setText(encryptionTypes[0], false)

        binding.spinnerEncryption.setOnItemClickListener { _, _, position, _ ->
            val lastIndex = binding.spinnerEncryption.adapter.count - 1

            val isLastSelected = (position == lastIndex)

            binding.containerPassword.visibility = if (isLastSelected) View.GONE else View.VISIBLE
        }
    }

    override fun validateAndBuildPayload(): QrCodeInfo? {
        val ssid = binding.etWifiSsid.text.toString().trim()
        val password = binding.etWifiPassword.text.toString().trim()
        val encryptionText = binding.spinnerEncryption.text.toString().trim()
        val isHidden = binding.switchIsHidden.isChecked

        if (ssid.isEmpty()) {
            binding.etWifiSsid.error = binding.root.context.getString(R.string.enter_network_name_ssid)
            return null
        }



        val passType = when {
            encryptionText.contains("WPA", ignoreCase = true) -> "WPA"
            encryptionText.contains("WEP", ignoreCase = true) -> "WEP"
            else -> "nopass"
        }

        val rawValue = "WIFI:T:$passType;S:$ssid;P:$password;H:$isHidden;;"
        val type = QrItemCreation.WIFI.toDbKey()
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
            binding.etSmsPhone.error = binding.root.context.getString(R.string.enter_phone_number)
            return null
        }

        val rawValue = "SMSTO:$phone:$message"
        val type = QrItemCreation.SMS.toDbKey()
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
        val type = QrItemCreation.PHONE.toDbKey()
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
        val type = QrItemCreation.EMAIL.toDbKey()
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
        val type = QrItemCreation.VCARD.toDbKey()
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
        val type = QrItemCreation.MeCARD.toDbKey()
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
        val type = QrItemCreation.LOCATION.toDbKey()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }
}

// 10. Event / VCalendar Handler (item_event_type.xml)
class EventFormHandler(private val binding: ItemEventTypeBinding, private val fragmentManager: FragmentManager) : QrFormHandler {
    init {
        binding.etStartDate.setOnClickListener {
            showCalendarDialog(startDate = true)
        }
        binding.etEndDate.setOnClickListener {
            showCalendarDialog(startDate = false)
        }
    }
    override fun validateAndBuildPayload(): QrCodeInfo? {
        val title = binding.etEventTitle.text.toString().trim()
        val startDate = binding.etStartDate.text.toString().trim()
        val endDate = binding.etEndDate.text.toString().trim()
        val location = binding.etEventLocation.text.toString().trim()
        val description = binding.etEventDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.etEventTitle.error = binding.root.context.getString(R.string.enter_event_title)
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
        val type = QrItemCreation.EVENT.toDbKey()
        val qrItem = QrCodeInfo(type, rawValue)
        return qrItem
    }

    private fun showCalendarDialog(startDate: Boolean) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.select_date)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectionInMillis ->
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val formattedDate = formatter.format(Date(selectionInMillis))

            if (startDate) {
                binding.etStartDate.setText(formattedDate)
            }
            else {
                binding.etEndDate.setText(formattedDate)
            }
        }

        datePicker.show(fragmentManager, "MATERIAL_DATE_PICKER")
    }
}