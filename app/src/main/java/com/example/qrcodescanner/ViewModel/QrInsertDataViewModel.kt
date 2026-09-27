package com.example.qrcodescanner.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcodescanner.enumClass.QrCreatorType
import com.google.android.datatransport.runtime.EncodedPayload
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class QrInsertDataViewModel : ViewModel() {
    private val _qrCreatorType = MutableSharedFlow<QrCreatorType>()
    val qrCreatorType: SharedFlow<QrCreatorType> = _qrCreatorType.asSharedFlow()


    fun shareQrCreatorType(qrCreatorType: QrCreatorType){
        viewModelScope.launch {
            _qrCreatorType.emit(qrCreatorType)
        }
    }
    fun save (payload: String){

    }
}