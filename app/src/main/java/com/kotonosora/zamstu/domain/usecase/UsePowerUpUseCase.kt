package com.kotonosora.zamstu.domain.usecase

import com.kotonosora.zamstu.domain.repository.SettingsRepository

class UsePowerUpUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(typeId: String): Boolean {
        return settingsRepository.usePowerUp(typeId)
    }
}
