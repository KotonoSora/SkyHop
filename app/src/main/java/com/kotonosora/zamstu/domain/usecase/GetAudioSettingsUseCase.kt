package com.kotonosora.zamstu.domain.usecase

import com.kotonosora.zamstu.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

data class AudioSettings(
    val musicEnabled: Boolean,
    val sfxEnabled: Boolean
)

class GetAudioSettingsUseCase(private val settingsRepository: SettingsRepository) {
    val musicEnabledFlow: Flow<Boolean> = settingsRepository.musicEnabledFlow
    val sfxEnabledFlow: Flow<Boolean> = settingsRepository.sfxEnabledFlow
}
