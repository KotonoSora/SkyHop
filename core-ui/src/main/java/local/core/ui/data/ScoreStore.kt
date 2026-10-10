package local.core.ui.data

import kotlinx.coroutines.flow.Flow

/**
 * Generic high-score + score-history persistence. Implemented by the DataStore
 * repository; game layers adapt their own domain types on top of this.
 */
interface ScoreStore {
    val highScoreFlow: Flow<Int>
    val scoreHistoryFlow: Flow<List<ScoreEntry>>
    suspend fun updateHighScore(score: Int)
    suspend fun saveScoreEntry(entry: ScoreEntry)
}
