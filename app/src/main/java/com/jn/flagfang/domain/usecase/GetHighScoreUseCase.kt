package com.jn.flagfang.domain.usecase

import com.jn.flagfang.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetHighScoreUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<Int> = scoreRepository.highScoreFlow
}
