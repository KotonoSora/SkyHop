package local.flappybird.engine.usecase

import local.flappybird.engine.repo.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetHighScoreUseCase(private val scoreRepository: ScoreRepository) {
    operator fun invoke(): Flow<Int> = scoreRepository.highScoreFlow
}
