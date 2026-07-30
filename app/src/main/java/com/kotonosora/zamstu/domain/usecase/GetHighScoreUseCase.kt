package com.kotonosora.zamstu.domain.usecase

import com.kotonosora.zamstu.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetHighScoreUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<Int> = scoreRepository.highScoreFlow
}
