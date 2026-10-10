package local.flappybird.engine.usecase

import local.flappybird.engine.repo.RunHistoryRepository
import kotlinx.coroutines.flow.Flow

class GetHighScoreUseCase(private val runs: RunHistoryRepository) {
    operator fun invoke(): Flow<Int> = runs.highScoreFlow
}
