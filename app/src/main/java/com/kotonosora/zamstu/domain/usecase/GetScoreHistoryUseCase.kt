package com.kotonosora.zamstu.domain.usecase

import com.kotonosora.zamstu.domain.model.ScoreEntry
import com.kotonosora.zamstu.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetScoreHistoryUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<List<ScoreEntry>> = scoreRepository.scoreHistoryFlow
}
