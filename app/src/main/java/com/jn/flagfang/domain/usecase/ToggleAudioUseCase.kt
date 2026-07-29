package com.jn.flagfang.domain.usecase

import com.jn.flagfang.domain.repository.SettingsRepository

class ToggleAudioUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun toggleMusic(enabled: Boolean) = settingsRepository.toggleMusic(enabled)
    suspend fun toggleSfx(enabled: Boolean) = settingsRepository.toggleSfx(enabled)
}
