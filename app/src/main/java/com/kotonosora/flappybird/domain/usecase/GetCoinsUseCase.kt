package com.kotonosora.flappybird.domain.usecase

import com.kotonosora.flappybird.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetCoinsUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<Int> = settingsRepository.coinsFlow
}
