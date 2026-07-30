package com.kotonosora.zamstu.domain.usecase

import com.kotonosora.zamstu.domain.repository.SettingsRepository

class ToggleAudioUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun toggleMusic(enabled: Boolean) = settingsRepository.toggleMusic(enabled)
    suspend fun toggleSfx(enabled: Boolean) = settingsRepository.toggleSfx(enabled)
}
