package com.example.qrcodescanner.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.qrcodescanner.SealedInterface.QrItemCreation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QrCreatorAndInsertSharedVM: ViewModel() {
    private val _selectedType = MutableStateFlow<QrItemCreation>(QrItemCreation.TEXT)
    val selectedType: StateFlow<QrItemCreation> = _selectedType


    fun selectQrType(type: QrItemCreation) {
        _selectedType.value = type
    }
}