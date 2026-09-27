package com.example.qrcodescanner.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcodescanner.DataBase.Tables.QrCodeItemTable
import com.example.qrcodescanner.Repository.QrRepository
import com.example.qrcodescanner.enumClass.QrFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QrCodeHistoryViewModel(private val repository: QrRepository) : ViewModel() {
    private val _selectedFilter = MutableStateFlow(QrFilter.ALL)
    val selectedFilter: StateFlow<QrFilter> = _selectedFilter.asStateFlow()
    val qrHistoryList: StateFlow<List<QrCodeItemTable>> = combine(
        repository.getAllItem(), // Поток из Room БД
        _selectedFilter          // Поток с текущим фильтром
    ) { itemsList, currentFilter ->
        // Логика фильтрации в памяти
        when (currentFilter) {
            QrFilter.ALL -> itemsList
            QrFilter.FAVORITE -> itemsList.filter { it.favorite }
            QrFilter.CREATED -> itemsList.filter { it.status.equals("Create", ignoreCase = true) }
            QrFilter.SCANNED -> itemsList.filter { it.status.equals("Scanned", ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Пауза при сворачивании
        initialValue = emptyList()
    )

    fun toggleFavorite(item: QrCodeItemTable) {
        val itemId = item.id ?: return
        viewModelScope.launch {
            repository.updateFavoriteStatus(itemId, !item.favorite)
        }
    }

    fun setFilter(filter: QrFilter) {
        _selectedFilter.value = filter
    }

}