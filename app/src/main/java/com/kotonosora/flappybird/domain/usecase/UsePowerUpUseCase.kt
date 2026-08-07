package com.kotonosora.flappybird.domain.usecase

import com.kotonosora.flappybird.domain.repository.SettingsRepository

class UsePowerUpUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(typeId: String): Boolean {
        return settingsRepository.usePowerUp(typeId)
    }
}
