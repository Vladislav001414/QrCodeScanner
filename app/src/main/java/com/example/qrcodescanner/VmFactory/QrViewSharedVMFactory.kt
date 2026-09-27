package com.example.qrcodescanner.VmFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Database
import com.example.qrcodescanner.DataBase.MovieDateBase
import com.example.qrcodescanner.Repository.QrRepository
import com.example.qrcodescanner.ViewModel.QrViewSharedVM

class QrViewSharedVMFactory(val context: Context): ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataBase = MovieDateBase.getInstance(appContext)
        val dao = dataBase.getDBDao()
        val repository = QrRepository(dao)
        return QrViewSharedVM(repository) as T
    }
}