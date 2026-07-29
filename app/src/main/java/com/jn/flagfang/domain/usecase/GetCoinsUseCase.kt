package com.jn.flagfang.domain.usecase

import com.jn.flagfang.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetCoinsUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<Int> = settingsRepository.coinsFlow
}
