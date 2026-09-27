package com.example.qrcodescanner.Handler

import com.example.qrcodescanner.DataClass.QrCodeInfo

interface QrFormHandler {
    fun validateAndBuildPayload(): QrCodeInfo?
}