package com.kotonosora.flappybird.domain.usecase

import com.kotonosora.flappybird.domain.model.ScoreEntry
import com.kotonosora.flappybird.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetScoreHistoryUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<List<ScoreEntry>> = scoreRepository.scoreHistoryFlow
}
