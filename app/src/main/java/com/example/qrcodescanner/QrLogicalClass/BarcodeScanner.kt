package com.example.qrcodescanner.QrLogicalClass

import android.util.Log
import com.example.qrcodescanner.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object BarcodeScanner {
    private val client = OkHttpClient()

    // Интерфейс-коллбэк для возврата данных во ViewModel
    interface BarcodeCallback {
        fun onSuccess(productName: String)
        fun onError(errorMessage: Int)
    }

    fun fetchProductInfo(barcode: String, callback: BarcodeCallback) {
        // 2. Формируем REST URL (эндпоинт)
        val url = "https://world.openfoodfacts.org/api/v0/product/$barcode.json"


        // 3. Создаем объект запроса (HTTP Request)
        val request = Request.Builder()
            .url(url)
            // Метод GET применяется по умолчанию, но мы можем указать его явно
            .get()
            // Добавляем обязательный заголовок для индентификации нашего приложения
            .header("User-Agent", "MyCVBarcodeApp/1.0 (Android; Kotlin)")
            .build()

        // 4. Отправляем запрос асинхронно в фоновый поток (Enqueue)
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.e("BarcodeRepo", "Сетевой сбой: ${e.localizedMessage}")
                callback.onError(R.string.error_network)
            }

            // Сработает, если сервер ответил (неважно, вернул он товар или ошибку 404)
            override fun onResponse(call: Call, response: Response) {
                // response.use автоматически закроет поток данных (Body), предотвращая утечки памяти
                response.use {

                    // Проверяем HTTP статус-код (response.isSuccessful означает код от 200 до 299)
                    if (!response.isSuccessful) {
                        callback.onError(R.string.error_server_with_code)
                        return
                    }

                    // Читаем сырое тело ответа в виде текста (Строки)
                    val responseBody = response.body?.string()
                    if (responseBody.isNullOrEmpty()) {
                        callback.onError(R.string.error_empty_response)
                        return
                    }

                    try {
                        // 5. Начинаем ручной парсинг JSON-строки
                        val jsonObject = JSONObject(responseBody)

                        // Проверяем статус внутри самого JSON (специфика Open Food Facts API)
                        // status = 1 означает, что товар найден. 0 — товара нет в базе.
                        val status = jsonObject.optInt("status", 0)

                        if (status == 1) {
                            // Извлекаем вложенный JSON-объект "product"
                            val productJson = jsonObject.getJSONObject("product")

                            // Достаем текстовое поле с названием товара
                            val productName = productJson.optString("product_name", barcode)

                            // Возвращаем результат во ViewModel
                            callback.onSuccess(productName)
                        } else {
                            callback.onError(R.string.error_product_not_found)
                        }

                    } catch (e: Exception) {
                        Log.e("BarcodeRepo", "Ошибка парсинга JSON", e)
                        callback.onError(R.string.error_invalid_server_data)
                    }
                }
            }
        })
    }
}
