package com.example.qrcodescanner.VmFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qrcodescanner.ViewModel.QrCreatorAndInsertSharedVM
import com.example.qrcodescanner.ViewModel.ScannerFragmentViewModel

class QrCreationAndInsertSharedVMFactory: ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QrCreatorAndInsertSharedVM() as T
    }
}