package com.example.qrcodescanner.DataClass

import com.example.qrcodescanner.enumClass.QrType
import com.google.mlkit.vision.barcode.common.Barcode

data class QrCodeResult(
    val rawValue: String,
    val displayValue: String,
    val type: QrType,
    val rawBarcode: Barcode
)