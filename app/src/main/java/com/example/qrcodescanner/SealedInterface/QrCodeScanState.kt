package com.example.qrcodescanner.SealedInterface

import com.example.qrcodescanner.DataClass.QrCodeResult

sealed interface QrCodeScanState {
    data class Success(val qrResult: QrCodeResult) : QrCodeScanState
    data class Error(val stringResId: Int) : QrCodeScanState
}