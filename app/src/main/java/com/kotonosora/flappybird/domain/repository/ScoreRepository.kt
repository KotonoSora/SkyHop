package com.kotonosora.flappybird.domain.repository

import com.kotonosora.flappybird.domain.model.ScoreEntry
import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    val highScoreFlow: Flow<Int>
    val scoreHistoryFlow: Flow<List<ScoreEntry>>
    suspend fun updateHighScore(score: Int)
    suspend fun saveScoreEntry(entry: ScoreEntry)
}
