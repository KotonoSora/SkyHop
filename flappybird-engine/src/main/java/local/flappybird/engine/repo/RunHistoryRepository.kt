package local.flappybird.engine.repo

import local.flappybird.engine.model.RunRecord
import kotlinx.coroutines.flow.Flow

interface RunHistoryRepository {
    val highScoreFlow: Flow<Int>
    fun recentRunsFlow(limit: Int): Flow<List<RunRecord>>

    /** Inserts a run, returning its row id for later [updateRun] calls. */
    suspend fun recordRun(record: RunRecord): Long

    /** Overwrites the row stored under [id] (e.g. after a revive continues the run). */
    suspend fun updateRun(id: Long, record: RunRecord)
}
