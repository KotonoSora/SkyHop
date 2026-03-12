package com.kotonosora.skyboundhopper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kotonosora.skyboundhopper.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    appVersionName: String
) : ViewModel() {

    /** App version string resolved once at creation time by the factory. */
    val versionName: String = appVersionName

    /** Backed by a hot StateFlow so the UI always has a cached value on first collection. */
    val musicEnabled: StateFlow<Boolean> = settingsRepository.musicEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val sfxEnabled: StateFlow<Boolean> = settingsRepository.sfxEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.toggleMusic(enabled) }
    }

    fun toggleSfx(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.toggleSfx(enabled) }
    }
}

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository,
    private val versionName: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsRepository, versionName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}