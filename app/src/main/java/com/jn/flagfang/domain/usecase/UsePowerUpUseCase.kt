package com.jn.flagfang.domain.usecase

import com.jn.flagfang.domain.repository.SettingsRepository

class UsePowerUpUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(typeId: String): Boolean {
        return settingsRepository.usePowerUp(typeId)
    }
}
