package com.jn.flagfang.domain.usecase

import com.jn.flagfang.domain.model.ScoreEntry
import com.jn.flagfang.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetScoreHistoryUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<List<ScoreEntry>> = scoreRepository.scoreHistoryFlow
}
