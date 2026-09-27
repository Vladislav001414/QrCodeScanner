package com.example.qrcodescanner.SealedInterface

import com.example.qrcodescanner.DataClass.QrCodeResult

sealed interface BarcodeScanState {
    data class Success(val qrResult: QrCodeResult) : BarcodeScanState
    data class Error(val stringResId: Int) : BarcodeScanState
}