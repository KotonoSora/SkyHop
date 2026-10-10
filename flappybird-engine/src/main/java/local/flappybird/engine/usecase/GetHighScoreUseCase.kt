package local.flappybird.engine.usecase

import kotlinx.coroutines.flow.Flow
import local.flappybird.engine.repo.RunHistoryRepository

class GetHighScoreUseCase(private val runs: RunHistoryRepository) {
    operator fun invoke(): Flow<Int> = runs.highScoreFlow
}
