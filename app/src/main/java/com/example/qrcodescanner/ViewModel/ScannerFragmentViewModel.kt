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

    private var isScanningAllowed = true


    private val _qrCodeEvent = MutableSharedFlow<BarcodeScanState>()
    val qrCodeEvent: SharedFlow<BarcodeScanState> = _qrCodeEvent.asSharedFlow()

    private val _flashIsWorking = MutableStateFlow<FlashState>(FlashState.Disabled)
    val flashIsWorking: StateFlow<FlashState> = _flashIsWorking

    private val _cameraSelector = MutableStateFlow(CameraSelector.DEFAULT_BACK_CAMERA)

    val cameraSelector = _cameraSelector.asStateFlow()


    fun onQrCodeDetected(qrResult: BarcodeScanState) {

        if (!isScanningAllowed) return


        isScanningAllowed = false


        viewModelScope.launch {


            _qrCodeEvent.emit(qrResult)


            delay(3000)


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

        _cameraSelector.value = if (_cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }


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