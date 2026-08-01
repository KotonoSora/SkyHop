package com.kotonosora.flappybird.domain.usecase

import com.kotonosora.flappybird.domain.repository.SettingsRepository

class ToggleAudioUseCase(private val settingsRepository: SettingsRepository) {
    suspend fun toggleMusic(enabled: Boolean) = settingsRepository.toggleMusic(enabled)
    suspend fun toggleSfx(enabled: Boolean) = settingsRepository.toggleSfx(enabled)
}
