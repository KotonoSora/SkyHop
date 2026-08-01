package com.kotonosora.flappybird.domain.usecase

import com.kotonosora.flappybird.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetHighScoreUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<Int> = scoreRepository.highScoreFlow
}
