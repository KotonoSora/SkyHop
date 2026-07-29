package com.jn.flagfang.domain.repository

import com.jn.flagfang.domain.model.ScoreEntry
import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    val highScoreFlow: Flow<Int>
    val scoreHistoryFlow: Flow<List<ScoreEntry>>
    suspend fun updateHighScore(score: Int)
    suspend fun saveScoreEntry(entry: ScoreEntry)
}
