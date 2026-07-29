package com.jn.flagfang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jn.flagfang.domain.usecase.GetAudioSettingsUseCase
import com.jn.flagfang.domain.usecase.GetCoinsUseCase
import com.jn.flagfang.domain.usecase.ToggleAudioUseCase
import com.jn.flagfang.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getAudioSettingsUseCase: GetAudioSettingsUseCase,
    private val toggleAudioUseCase: ToggleAudioUseCase,
    private val getCoinsUseCase: GetCoinsUseCase,
    private val settingsRepository: SettingsRepository,
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

    val canClaimDailyReward: StateFlow<Boolean> = settingsRepository.lastDailyRewardTimeFlow.map { lastTime ->
        val now = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000L
        now - lastTime >= oneDayInMillis
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun toggleMusic(enabled: Boolean) {
        viewModelScope.launch { toggleAudioUseCase.toggleMusic(enabled) }
    }

    fun toggleSfx(enabled: Boolean) {
        viewModelScope.launch { toggleAudioUseCase.toggleSfx(enabled) }
    }

    fun claimReward() {
        viewModelScope.launch {
            settingsRepository.claimDailyReward()
        }
    }
}
