package com.example.qrcodescanner.HandlerFactory

import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.qrcodescanner.Handler.ContactFormHandler
import com.example.qrcodescanner.Handler.EmailFormHandler
import com.example.qrcodescanner.Handler.EventFormHandler
import com.example.qrcodescanner.Handler.GpsFormHandler
import com.example.qrcodescanner.Handler.MyQrFormHandler
import com.example.qrcodescanner.Handler.PhoneFormHandler
import com.example.qrcodescanner.Handler.QrFormHandler
import com.example.qrcodescanner.Handler.SmsFormHandler
import com.example.qrcodescanner.Handler.TextFormHandler
import com.example.qrcodescanner.Handler.UrlFormHandler
import com.example.qrcodescanner.Handler.WifiFormHandler
import com.example.qrcodescanner.SealedInterface.QrItemCreation
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

object QrFormHandlerFactory {
    fun createHandler(type: QrItemCreation, containerView: View, fragmentManager: FragmentManager): QrFormHandler {
        return when (type) {
            QrItemCreation.URL -> UrlFormHandler(ItemUrlTypeBinding.bind(containerView))
            QrItemCreation.TEXT -> TextFormHandler(ItemTextTypeBinding.bind(containerView))
            QrItemCreation.WIFI -> WifiFormHandler(ItemWifiTypeBinding.bind(containerView))
            QrItemCreation.SMS -> SmsFormHandler(ItemSmsTypeBinding.bind(containerView))
            QrItemCreation.PHONE -> PhoneFormHandler(ItemPhoneTypeBinding.bind(containerView))
            QrItemCreation.EMAIL -> EmailFormHandler(ItemEmailTypeBinding.bind(containerView))
            QrItemCreation.VCARD -> ContactFormHandler(ItemContactTypeBinding.bind(containerView))
            QrItemCreation.MeCARD -> MyQrFormHandler(ItemMyqrTypeBinding.bind(containerView))
            QrItemCreation.LOCATION -> GpsFormHandler(ItemGpsTypeBinding.bind(containerView))
            QrItemCreation.EVENT -> EventFormHandler(ItemEventTypeBinding.bind(containerView), fragmentManager)
        }
    }
}