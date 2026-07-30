package com.kotonosora.zamstu.domain.usecase

import com.kotonosora.zamstu.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetCoinsUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<Int> = settingsRepository.coinsFlow
}
