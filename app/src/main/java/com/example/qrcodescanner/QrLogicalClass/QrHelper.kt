package com.example.qrcodescanner.QrLogicalClass

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object QrHelper {

    fun saveQrToGallery(context: Context, bitmap: Bitmap, filename: String = "qr_code_${System.currentTimeMillis()}.png") {
        val imageUri: Uri?
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")

        }

        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri != null) {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                    Toast.makeText(context, "QR-код сохранен в галерею!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Ошибка при сохранении", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun shareQrCode(context: Context, bitmap: Bitmap) {
        try {
            // Сохраняем во временную папку cache
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_qr.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            // Получаем безопасный Uri через FileProvider
            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Предоставляем доступ другим приложениям
                    setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    putExtra(Intent.EXTRA_TEXT, "Вот мой QR-код:")
                    type = "image/png"
                }

                // Запуск системного меню выборки приложений
                context.startActivity(Intent.createChooser(shareIntent, "Поделиться QR-кодом"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Ошибка при попытке поделиться", Toast.LENGTH_SHORT).show()
        }
    }
}