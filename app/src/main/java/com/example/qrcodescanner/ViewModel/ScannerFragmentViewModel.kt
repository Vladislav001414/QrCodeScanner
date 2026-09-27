package com.example.qrcodescanner.ViewModel

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import com.example.qrcodescanner.DataClass.QrCodeResult
import com.example.qrcodescanner.Repository.QrRepository
import com.example.qrcodescanner.SealedInterface.BarcodeScanState
import com.example.qrcodescanner.SealedInterface.FlashState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScannerFragmentViewModel(val repository: QrRepository) : ViewModel() {
        // Переменная блокировки переехала сюда и стала приватной
    private var isScanningAllowed = true

    // Канал для отправки проверенного текста обратно во фрагмент
    private val _qrCodeEvent = MutableSharedFlow<BarcodeScanState>()
    val qrCodeEvent: SharedFlow<BarcodeScanState> = _qrCodeEvent.asSharedFlow()

    private val _flashIsWorking = MutableStateFlow<FlashState>(FlashState.Disabled)
    val flashIsWorking: StateFlow<FlashState> = _flashIsWorking

    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)

    val cameraSelector = _cameraSelector.asStateFlow()

    // Метод, который принимает считанный текст от камеры
    fun onQrCodeDetected(qrResult: BarcodeScanState) {
        // Если сканирование сейчас заблокировано таймером — просто игнорируем этот кадр
        if (!isScanningAllowed) return

        // Блокируем сканирование, чтобы другие кадры не проходили
        isScanningAllowed = false

        // Запускаем безопасную корутину в контексте ViewModel
        viewModelScope.launch {
            // Отправляем текст во Фрагмент для показа на экране

            _qrCodeEvent.emit(qrResult)

            // Ждем 3 секунды паузы прямо здесь (замена postDelayed)
            delay(3000)

            // Разблокируем сканирование для следующих кодов
            isScanningAllowed = true
        }
    }

    fun saveNewScan(result: QrCodeResult){
        viewModelScope.launch(Dispatchers.IO) {
            val qrItem = QrCodeItemTable(
                id = null,
                qrType = result.type.keyToDB,
                rawValue = result.rawValue,
                status = "Scanned",
                favorite = false,
                dateAdded = System.currentTimeMillis()
            )
            repository.saveQrCode(qrItem)
        }
    }

    fun onFlashButtonClick(){
        viewModelScope.launch {
            when(_flashIsWorking.value){
                FlashState.OnForward, FlashState.OnBackward -> _flashIsWorking.value = FlashState.Disabled
                FlashState.Disabled -> {
                    if (_cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                        _flashIsWorking.value = FlashState.OnBackward
                    } else {
                        _flashIsWorking.value = FlashState.OnForward
                    }

                }
            }
        }
    }
    fun switchCamera() {
        // 1. Сначала переключаем камеру (синхронно, без корутин)
        _cameraSelector.value = if (_cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        // 2. Красиво и лаконично обновляем вспышку
        _flashIsWorking.value = when (_flashIsWorking.value) {
            FlashState.OnForward -> {
                if (_cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) FlashState.OnBackward else FlashState.OnForward
            }
            FlashState.OnBackward -> {
                if (_cameraSelector.value == CameraSelector.DEFAULT_FRONT_CAMERA) FlashState.OnForward else FlashState.OnBackward
            }
            FlashState.Disabled -> FlashState.Disabled
        }
    }
}