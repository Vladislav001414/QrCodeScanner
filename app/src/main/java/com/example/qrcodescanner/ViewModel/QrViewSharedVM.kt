package com.example.qrcodescanner.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import com.example.qrcodescanner.DataClass.QrCodeInfo
import com.example.qrcodescanner.Repository.QrRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QrViewSharedVM(private val repository: QrRepository) : ViewModel() {
    private val _qrSelectedItem = MutableStateFlow<QrCodeItemTable?>(null)
    val qrSelectedItem: StateFlow<QrCodeItemTable?> = _qrSelectedItem.asStateFlow()

    fun saveNewQrItem(qrItem: QrCodeItemTable){
        viewModelScope.launch {

            repository.saveQrCode(qrItem)

            _qrSelectedItem.value = qrItem
        }
    }

}