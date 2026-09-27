package com.example.qrcodescanner.ViewModel

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrcodescanner.DataBase.Tables.ProfileTable
import com.example.qrcodescanner.QrLogicalClass.SettingsDefaults
import com.example.qrcodescanner.Repository.QrRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(val repository: QrRepository) : ViewModel() {

    private val _profileSettings = MutableStateFlow<ProfileTable?>(null)
    val profileSettings: StateFlow<ProfileTable?> = _profileSettings.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllProfileSettings().collect { settings ->
                if(settings != null) {
                    _profileSettings.value = settings

                }
                else saveDefaultSettings()
            }
        }
    }

    private fun saveDefaultSettings(){
        val defaultLanguage = SettingsDefaults.getDefaultLanguageCode()
        val defaultTheme = SettingsDefaults.getDefaultThemeMode()
        val initialSettings = ProfileTable(
            id = null,
            language = defaultLanguage,
            themeMode = defaultTheme)

        viewModelScope.launch {
            repository.saveDefProfileSettings(initialSettings)
        }
    }

    fun updateTheme(themeMode: Int){
        viewModelScope.launch {
            repository.updateThemeMode(themeMode)

        }
    }
    fun updateLanguage(currentLanguage: String){
        viewModelScope.launch {
            repository.updateLanguage(currentLanguage)
        }
    }
}