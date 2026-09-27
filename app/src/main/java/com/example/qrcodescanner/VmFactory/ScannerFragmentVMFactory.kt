package com.example.qrcodescanner.VmFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.qrcodescanner.DataBase.MovieDateBase
import com.example.qrcodescanner.Repository.QrRepository
import com.example.qrcodescanner.ViewModel.ScannerFragmentViewModel

class ScannerFragmentVMFactory(context: Context): ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataBase = MovieDateBase.getInstance(appContext)
        val dao = dataBase.getDBDao()
        val repository = QrRepository(dao)
        return ScannerFragmentViewModel(repository) as T
    }
}