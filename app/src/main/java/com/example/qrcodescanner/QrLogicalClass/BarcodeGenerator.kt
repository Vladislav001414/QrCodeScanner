package com.example.qrcodescanner.QrLogicalClass

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

object BarcodeGenerator {

    fun generateBarcode(
        content: String,
        format: BarcodeFormat,
        width: Int = 600,
        height: Int = 600
    ): Bitmap? {
        if (content.isEmpty()) return null

        return try {
            val barcodeEncoder = BarcodeEncoder()

            barcodeEncoder.encodeBitmap(content, format, width, height)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}