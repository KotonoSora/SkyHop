package com.jn.flagfang.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jn.flagfang.domain.model.ScoreEntry
import com.jn.flagfang.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.max

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ScoreRepositoryImpl(private val context: Context) : ScoreRepository {

    companion object {
        private val HIGH_SCORE_KEY = intPreferencesKey("high_score")
        private val SCORE_HISTORY_KEY = stringPreferencesKey("score_history")
        private const val MAX_HISTORY_SIZE = 20
    }

    override val highScoreFlow: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[HIGH_SCORE_KEY] ?: 0
        }

    override val scoreHistoryFlow: Flow<List<ScoreEntry>> = context.dataStore.data
        .map { preferences ->
            preferences[SCORE_HISTORY_KEY]
                ?.split(";")
                ?.mapNotNull { raw ->
                    val parts = raw.split(":")
                    if (parts.size == 3) {
                        ScoreEntry(
                            score = parts[0].toIntOrNull() ?: 0,
                            reward = parts[1].toIntOrNull() ?: 0,
                            timestamp = parts[2].toLongOrNull() ?: 0L
                        )
                    } else null
                }
                ?: emptyList()
        }

    override suspend fun updateHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[HIGH_SCORE_KEY] ?: 0
            preferences[HIGH_SCORE_KEY] = max(currentHighScore, score)
        }
    }

    override suspend fun saveScoreEntry(entry: ScoreEntry) {
        if (entry.score <= 0) return
        context.dataStore.edit { preferences ->
            val existingRaw = preferences[SCORE_HISTORY_KEY] ?: ""
            val existingEntries = existingRaw.split(";")
                .filter { it.isNotBlank() }
            
            val newRaw = "${entry.score}:${entry.reward}:${entry.timestamp}"
            val updatedEntries = (listOf(newRaw) + existingEntries)
                .take(MAX_HISTORY_SIZE)
            
            preferences[SCORE_HISTORY_KEY] = updatedEntries.joinToString(";")
        }
    }
}
