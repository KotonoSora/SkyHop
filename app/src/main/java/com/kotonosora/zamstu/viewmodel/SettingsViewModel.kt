package com.kotonosora.zamstu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotonosora.zamstu.domain.repository.SettingsRepository
import com.kotonosora.zamstu.domain.usecase.GetAudioSettingsUseCase
import com.kotonosora.zamstu.domain.usecase.GetCoinsUseCase
import com.kotonosora.zamstu.domain.usecase.ToggleAudioUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SettingsIntent {
    data class ToggleMusic(val enabled: Boolean) : SettingsIntent()
    data class ToggleSfx(val enabled: Boolean) : SettingsIntent()
}

class SettingsViewModel(
    private val getAudioSettingsUseCase: GetAudioSettingsUseCase,
    private val toggleAudioUseCase: ToggleAudioUseCase,
    private val getCoinsUseCase: GetCoinsUseCase,
    appVersionName: String
) : ViewModel() {

    /** App version string resolved once at creation time by the factory. */
    val versionName: String = appVersionName

    val coins: StateFlow<Int> = getCoinsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    /** Backed by a hot StateFlow so the UI always has a cached value on first collection. */
    val musicEnabled: StateFlow<Boolean> = getAudioSettingsUseCase.musicEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val sfxEnabled: StateFlow<Boolean> = getAudioSettingsUseCase.sfxEnabledFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToggleMusic -> toggleMusic(intent.enabled)
            is SettingsIntent.ToggleSfx -> toggleSfx(intent.enabled)
        }
    }

    private fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch { toggleAudioUseCase.toggleMusic(enabled) }
    }

    private fun toggleSfx(enabled: Boolean) {
        viewModelScope.launch { toggleAudioUseCase.toggleSfx(enabled) }
    }
}
