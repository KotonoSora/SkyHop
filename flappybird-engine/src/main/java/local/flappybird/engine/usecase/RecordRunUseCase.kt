package local.flappybird.engine.usecase

import local.flappybird.engine.model.RunRecord
import local.flappybird.engine.repo.RunHistoryRepository

class RecordRunUseCase(private val runs: RunHistoryRepository) {
    suspend operator fun invoke(record: RunRecord): Long = runs.recordRun(record)
}
