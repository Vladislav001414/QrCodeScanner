package com.example.qrcodescanner.QrLogicalClass

import android.content.Context
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.core.content.ContextCompat
import com.example.qrcodescanner.DataClass.QrCodeResult
import com.example.qrcodescanner.R
import com.example.qrcodescanner.View.ScannerOverlayView
import com.example.qrcodescanner.SealedInterface.BarcodeScanState
import com.example.qrcodescanner.enumClass.QrType
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val context: Context,
    private val onQrCodeScanned: (BarcodeScanState) -> Unit
){



    private val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    private val scanner = BarcodeScanning.getClient(options)

    @OptIn(ExperimentalGetImage::class)

    fun attachToCamera(
        cameraController: LifecycleCameraController,
        overlayView: ScannerOverlayView
    ) {

        val mlKitAnalyzer = MlKitAnalyzer(
            listOf(scanner),
            ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
            ContextCompat.getMainExecutor(context)
        ) { result ->

            val barcodes: List<Barcode>? = result.getValue(scanner)


            if (!barcodes.isNullOrEmpty()) {
                for (barcode in barcodes) {
                    val qrRectOnScreen: Rect = barcode.boundingBox ?: continue

                    val qrCenterX = qrRectOnScreen.centerX().toFloat()
                    val qrCenterY = qrRectOnScreen.centerY().toFloat()

                    val targetFrame = overlayView.boxRect

                    if (targetFrame.contains(qrCenterX, qrCenterY)) {

                        processDetectedBarcode(barcode)
                        break

                    }
                }
            }
        }
        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context),
            mlKitAnalyzer
        )
    }

    fun analyzeStaticImage(fileUri: Uri){
        try {
            val image = InputImage.fromFilePath(context, fileUri)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    val barcode = barcodes.firstOrNull()

                    if (barcode != null) {
                        processDetectedBarcode(barcode)
                    }
                    else{
                        onQrCodeScanned(BarcodeScanState.Error(R.string.error_qr_not_found))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("QR_ANALYZER", "ML Kit failed to process image", e)
                    onQrCodeScanned(BarcodeScanState.Error(R.string.error_gallery_scan_failed))
                }
        }
        catch (e: Exception){
            Log.e("QR_ANALYZER", "ML Kit failed to process image", e)
        }
    }


    private fun processDetectedBarcode(barcode: Barcode) {
        val qrText = barcode.rawValue ?: return
        val qrType = QrType.fromMlKitType(barcode.valueType)
        val displayText = when (barcode.valueType) {

            Barcode.TYPE_WIFI -> {
                val wifiName = barcode.wifi?.ssid ?: R.string.unknown_wifi
                "Wi-Fi: $wifiName"
            }

            Barcode.TYPE_CONTACT_INFO -> barcode.contactInfo?.name?.formattedName ?: qrText

            else -> barcode.displayValue ?: qrText
        }


        onQrCodeScanned(BarcodeScanState.Success(QrCodeResult(qrText, displayText, qrType, barcode)))
    }


}