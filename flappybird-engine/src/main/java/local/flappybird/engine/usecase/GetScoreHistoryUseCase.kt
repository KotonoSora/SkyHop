package local.flappybird.engine.usecase

import local.flappybird.engine.model.ScoreEntry
import local.flappybird.engine.repo.RunHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetScoreHistoryUseCase(private val runs: RunHistoryRepository) {
    operator fun invoke(limit: Int = DEFAULT_LIMIT): Flow<List<ScoreEntry>> =
        runs.recentRunsFlow(limit).map { list -> list.map { it.toScoreEntry() } }

    companion object {
        const val DEFAULT_LIMIT = 20
    }
}
