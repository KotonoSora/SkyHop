package local.flappybird.engine.usecase

import local.flappybird.engine.model.RunRecord
import local.flappybird.engine.repo.RunHistoryRepository

class UpdateRunUseCase(private val runs: RunHistoryRepository) {
    suspend operator fun invoke(id: Long, record: RunRecord) = runs.updateRun(id, record)
}
