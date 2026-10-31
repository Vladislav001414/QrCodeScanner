package com.example.qrcodescanner.Repository

import com.example.qrcodescanner.QrLogicalClass.BarcodeScanner
import com.example.qrcodescanner.SealedInterface.BarcodeScanState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BarcodeRepository {

    // Функция принимает свой собственный коллбэк
    fun getDataFromBarcode(barcode: String, onResult: (BarcodeScanState) -> Unit) {

        BarcodeScanner.fetchProductInfo(
            barcode = barcode,
            callback = object : BarcodeScanner.BarcodeCallback {
                override fun onSuccess(productName: String) {
                    // Передаем стейт успеха наружу
                    onResult(BarcodeScanState.Success(productName))
                }

                override fun onError(errorMessage: Int) {
                    // Передаем стейт ошибки наружу
                    onResult(BarcodeScanState.Error(errorMessage))
                }
            }
        )
    }
}

