package com.example.qrcodescanner.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import com.example.qrcodescanner.DataClass.QrCodeInfo
import com.example.qrcodescanner.Repository.QrRepository
import com.google.common.math.LongMath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QrViewSharedVM(private val repository: QrRepository) : ViewModel() {
    private val _qrSelectedItem = MutableStateFlow<QrCodeItemTable?>(null)
    val qrSelectedItem: StateFlow<QrCodeItemTable?> = _qrSelectedItem.asStateFlow()

    private var collectJob: Job? = null

    fun saveNewQrItem(qrItem: QrCodeItemTable){
        viewModelScope.launch {

            val id = repository.saveQrCode(qrItem)
            getSelectedItem(id)

        }
    }

    fun updateFavoriteStatus(){
        val qrItem = _qrSelectedItem.value ?: return
        val id = qrItem.id
        val currentFavorite = !qrItem.favorite


        viewModelScope.launch {
            if (id != null) {
                repository.updateFavoriteStatus(id, currentFavorite)
            }
        }
    }

    fun getSelectedItem(id: Long){

        collectJob?.cancel()

        collectJob = viewModelScope.launch {
            repository.getQrItemById(id).collect { item ->
                _qrSelectedItem.value = item
            }
        }
    }
}