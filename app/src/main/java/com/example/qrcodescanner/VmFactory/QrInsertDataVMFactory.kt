package com.example.qrcodescanner.VmFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qrcodescanner.ViewModel.QrInsertDataViewModel

class QrInsertDataVMFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QrInsertDataViewModel() as T
    }
}