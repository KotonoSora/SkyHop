package local.flappybird.engine.usecase

import local.flappybird.engine.model.ScoreEntry
import local.flappybird.engine.repo.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetScoreHistoryUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<List<ScoreEntry>> = scoreRepository.scoreHistoryFlow
}
