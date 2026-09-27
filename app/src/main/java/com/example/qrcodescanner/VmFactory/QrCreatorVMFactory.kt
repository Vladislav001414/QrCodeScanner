package com.example.qrcodescanner.VmFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qrcodescanner.ViewModel.QrCreatorViewModel

class QrCreatorVMFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QrCreatorViewModel() as T
    }
}